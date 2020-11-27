package com.example.halloworld.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.example.halloworld.MainActivity;
import com.example.halloworld.Model.User;
import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    NotificationCompat.Builder notificationBuilder;
    String title;
    String body ;
    Boolean isFriendRequest;
    String notificationType;
    User senderUser;
    User responseUser;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String,String> map = remoteMessage.getData();
        title= map.get("title");
        body = map.get("body");
        notificationType =map.get("notificationType");


        String userSenderString = map.get("userObjectSender");
        String userResponseString = map.get("userObjectResponse");
        Gson g = new Gson();
        senderUser = g.fromJson(userSenderString, User.class);
        responseUser = g.fromJson(userResponseString, User.class);

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

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","-")).setValue(new userToken(task.getResult()));
                quickLogout();
            }
        });
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


    private void trinkRequest(){

        int notificationID = (int)System.currentTimeMillis();

        Intent mainInten = new Intent(this, MainActivity.class);
        PendingIntent mainPending = PendingIntent.getActivity(this,0,mainInten,0);

        notificationBuilder = new NotificationCompat.Builder(this,"test")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.bier)
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

    private void trinkReply(){

        int notificationID = (int)System.currentTimeMillis();

        Intent mainInten = new Intent(this, MainActivity.class);
        PendingIntent mainPending = PendingIntent.getActivity(this,0,mainInten,0);

        notificationBuilder = new NotificationCompat.Builder(this,"test")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.bier)
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

    private void friendRequest(){

        int notificationID = (int)System.currentTimeMillis();
        Intent serviceIntent = new Intent(this, FriendRequestService.class);
        serviceIntent.putExtra("notificationType",notificationType)
                .putExtra(getString(R.string.NOTIFICATION_ID_KEY_FRIEND_REQUEST),notificationID)
                .putExtra("senderUser",senderUser)
                .putExtra("responseUser",responseUser);
        PendingIntent actionIntent = PendingIntent.getService(this,1,serviceIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent mainInten = new Intent(this, MainActivity.class);
        PendingIntent mainPending = PendingIntent.getActivity(this,2,mainInten,0);


        notificationBuilder = new NotificationCompat.Builder(this,"test")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.bier)
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
        FirebaseMessaging.getInstance().subscribeToTopic(responseUser.getEmail().replace("@","AT"));

        String senderEmailKey= generateEmailkey(senderUser.getEmail());
        String senderResponseEmailkey=generateEmailkey(responseUser.getEmail());

        FirebaseDatabase.getInstance().getReference("Friend").child(senderEmailKey).child(senderResponseEmailkey).setValue(responseUser.getEmail());
        FirebaseDatabase.getInstance().getReference("Friend").child(senderResponseEmailkey).child(senderEmailKey).setValue(senderUser.getEmail());
        Intent mainInten = new Intent(this, MainActivity.class);
        PendingIntent mainPending = PendingIntent.getActivity(this,0,mainInten,0);
        notificationBuilder = new NotificationCompat.Builder(this,"test")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.bier)
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
    public void quickLogout(){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        googleSignInClient.signOut();
        firebaseAuth.signOut();
        UserLocalStore userLocalStore= new UserLocalStore(this);
        userLocalStore.clearUserData();
        LoginManager.getInstance().logOut();
    }

    public String generateEmailkey(String email){
   /*     String senderResponseEmailkey;
        String senderResponseEmailFirstPart;
        String senderResponseEmailLastPart;
        int index2 =email.lastIndexOf(".");
        senderResponseEmailLastPart= email.substring(index2).replace(".","&");
        senderResponseEmailFirstPart=email.substring(0,index2);;
        senderResponseEmailkey = senderResponseEmailFirstPart+senderResponseEmailLastPart;
        return senderResponseEmailkey;*/
        return email.replace(".","&");
    }

    public NotificationCompat.Action generateDirectReplyAction(int notificationID){

        if(Build.VERSION.SDK_INT >= 24){
            Intent directReplyIntent = new Intent(this,DirectReplyService.class);


            directReplyIntent.putExtra(getString(R.string.NOTIFICATION_ID_KEY_TRINK_REQUEST),notificationID)
                    .putExtra("senderUser",senderUser);
            PendingIntent directReplyPendingIntent = PendingIntent.getService(this,4,directReplyIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteInput remoteInput = new RemoteInput.Builder(getString(R.string.NOTIFICATION_ID_KEY_TRINK_REPLY))
                    .setLabel("Nachricht eingeben").build();
            NotificationCompat.Action directReplyAction = new NotificationCompat.Action.Builder(R.drawable.bier,"Antworten",directReplyPendingIntent)
                    .addRemoteInput(remoteInput).build();
            return directReplyAction;
        }else{
            Intent mainInten = new Intent(this, MainActivity.class);
            PendingIntent mainPending = PendingIntent.getActivity(this,5,mainInten,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action directReplyAction = new NotificationCompat.Action.Builder(R.drawable.bier,"Antworten",mainPending)
            .build();
            return directReplyAction;

        }
    }
}