package com.example.halloworld.DesignV1.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import com.example.halloworld.DesignV1.HomeScreen;
import com.example.halloworld.Model.User;
import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

public class PushNotificationReciverService extends FirebaseMessagingService
{

    String title;
    String body;
    String notificationType;
    User userSender;
    User userReciver;
    UserLocalStore userLocalStore;
    NotificationCompat.Builder notificationBuilder;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String,String> map = remoteMessage.getData();
        title= map.get("title");
        body = map.get("body");
        notificationType =map.get("notificationType");


        String userSenderString = map.get("userObjectSender");
        String userReciverString = map.get("userObjectReciver");
        Gson g = new Gson();
        userSender = g.fromJson(userSenderString, User.class);
        userReciver = g.fromJson(userReciverString, User.class);

        if(notificationType.equals("friendRequest") ){

            friendRequest();
        }
        if(notificationType.equals("trinkRequest")){
            trinkRequest();
        }

        if(notificationType.equals("friendRequestResponse")){
            friendRequestResponse();
        }
        if(notificationType.equals("trinkReply")){
            trinkReply();
        }

    }

    private void trinkRequest(){

        int notificationID = (int)System.currentTimeMillis();

        Intent mainInten = new Intent(this, HomeScreen.class);
        PendingIntent mainPending = PendingIntent.getActivity(this,0,mainInten,0);

        notificationBuilder = new NotificationCompat.Builder(this,"test")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.anstossen)
                .setAutoCancel(true)
                .setContentIntent(mainPending)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setLights(Color.WHITE,1000,5000)
                .setVibrate(new long[]{0,300,300,300})
                .addAction(generateDirectReplyAction(notificationID));

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("test","demo",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(notificationID,notificationBuilder.build());

    }

    private void friendRequest(){

        int notificationID = (int)System.currentTimeMillis();
        Intent serviceIntent = new Intent(this, FriendRequestService.class);
        serviceIntent.putExtra("notificationType",notificationType)
                .putExtra(getString(R.string.NOTIFICATION_ID_KEY_FRIEND_REQUEST),notificationID)
                .putExtra("userSender",userSender)
                .putExtra("userReciver",userReciver);
        PendingIntent actionIntent = PendingIntent.getService(this,1,serviceIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent mainInten = new Intent(this, HomeScreen.class);
        PendingIntent mainPending = PendingIntent.getActivity(this,2,mainInten,0);


        notificationBuilder = new NotificationCompat.Builder(this,"test")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.anstossen)
                .setAutoCancel(true)
                .setContentIntent(mainPending)
                .addAction(R.drawable.ic_home,"Bestätigen",actionIntent)
                .addAction(R.drawable.ic_home,"Ablehnen",mainPending)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setLights(Color.WHITE,1000,5000)
                .setVibrate(new long[]{0,300,300,300});

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("test","demo",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(notificationID,notificationBuilder.build());
    }


    private void friendRequestResponse(){
        FirebaseMessaging.getInstance().subscribeToTopic(userReciver.getEmail().replace("@","AT"));

        String senderEmailKey= generateEmailkey(userSender.getEmail());
        String senderResponseEmailkey=generateEmailkey(userReciver.getEmail());

        FirebaseDatabase.getInstance().getReference("Friend").child(senderEmailKey).child(senderResponseEmailkey).setValue(userReciver.getEmail());
        FirebaseDatabase.getInstance().getReference("Friend").child(senderResponseEmailkey).child(senderEmailKey).setValue(userSender.getEmail());
        Intent mainInten = new Intent(this, HomeScreen.class);
        PendingIntent mainPending = PendingIntent.getActivity(this,0,mainInten,0);
        notificationBuilder = new NotificationCompat.Builder(this,"test")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.anstossen)
                .setAutoCancel(true)
                .setContentIntent(mainPending)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setLights(Color.WHITE,1000,5000)
                .setVibrate(new long[]{0,300,300,300});

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        int id = (int)System.currentTimeMillis();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("test","demo",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(id,notificationBuilder.build());
    }

    private void trinkReply(){

        int notificationID = (int)System.currentTimeMillis();

        Intent mainInten = new Intent(this, HomeScreen.class);
        PendingIntent mainPending = PendingIntent.getActivity(this,0,mainInten,0);

        notificationBuilder = new NotificationCompat.Builder(this,"test")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.anstossen)
                .setAutoCancel(true)
                .setContentIntent(mainPending)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setLights(Color.WHITE,1000,5000)
                .setVibrate(new long[]{0,300,300,300});


        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("test","demo",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(notificationID,notificationBuilder.build());

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                FirebaseDatabase.getInstance().getReference().child("User").child(generateEmailkey(FirebaseAuth.getInstance().getCurrentUser().getEmail())).setValue(new userToken(task.getResult()));
                refreshToken(task.getResult());
            }
        });
    }

    public String generateEmailkey(String email){
        return email.replace(".","&");
    }

    public class userToken{

        String usertoken;

        public userToken(String usertoken){

            this.usertoken=usertoken;
        }
        public String getUsertoken() {
            return usertoken;
        }

        public void setUsertoken(String usertoken) {
            this.usertoken = usertoken;
        }
    }

   private void refreshToken(String tocken){
        userLocalStore = new UserLocalStore(this);
        User user= userLocalStore.getLoggedInUser();
        user.setUserToken(tocken);
        userLocalStore.storeUserData(user);
   }

    public NotificationCompat.Action generateDirectReplyAction(int notificationID){

        if(Build.VERSION.SDK_INT >= 24){
            Intent directReplyIntent = new Intent(this, DirectReplyService.class);

            directReplyIntent.putExtra(getString(R.string.NOTIFICATION_ID_KEY_TRINK_REQUEST),notificationID)
                    .putExtra("userSender",userSender);
            PendingIntent directReplyPendingIntent = PendingIntent.getService(this,4,directReplyIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteInput remoteInput = new RemoteInput.Builder(getString(R.string.NOTIFICATION_ID_KEY_TRINK_REPLY))
                    .setLabel("Nachricht eingeben").build();
            NotificationCompat.Action directReplyAction = new NotificationCompat.Action.Builder(R.drawable.anstossen,"Antworten",directReplyPendingIntent)
                    .addRemoteInput(remoteInput).build();
            return directReplyAction;
        }else{
            Intent mainInten = new Intent(this, HomeScreen.class);
            PendingIntent mainPending = PendingIntent.getActivity(this,5,mainInten,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action directReplyAction = new NotificationCompat.Action.Builder(R.drawable.anstossen,"Antworten",mainPending)
                    .build();
            return directReplyAction;

        }
    }
}
