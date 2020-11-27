package com.example.halloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.halloworld.Enum.LoginType;
import com.example.halloworld.Model.User;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends DrawerMenu {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    UserLocalStore userLocalStore;
    FirebaseAuth firebaseAuth;
    AppEventsLogger logger;
    TextView txLogedIN;
    TextView txLogedINEmail;
    TextView txLogedINName;
    TextView txLogedINUUID;
    TextView txLogedINProvider;
    TextView txLogedINToken;
    Button trinkBtn;
    User user;
    String userTocken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        super.setDrawerLayout(this, toolbar, R.id.nav_home);
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userLocalStore = new UserLocalStore(this);
        logger = AppEventsLogger.newLogger(this);
        logSentFriendRequestEvent();
        txLogedIN = findViewById(R.id.isLogedIn);
        txLogedINEmail = findViewById(R.id.LogedInEmail);
        txLogedINName = findViewById(R.id.LogedInName);
        txLogedINUUID = findViewById(R.id.LogedInUUID);
        txLogedINProvider = findViewById(R.id.LogedInProvider);
        txLogedINToken = findViewById(R.id.LogedInToken);
        trinkBtn = findViewById(R.id.trinkBtn);
        firebaseAuth= FirebaseAuth.getInstance();

/*        if(getIntent().hasExtra(getString(R.string.NOTIFICATION_ID_KEY_FRIEND_REQUEST))){
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(getIntent().getIntExtra(getString(R.string.NOTIFICATION_ID_KEY_FRIEND_REQUEST),0));

        }*/

        if(userLocalStore.isUserLoggedIn()){
            txLogedIN.setText("User is Loged IN");
            txLogedINEmail.setText("User Email: "+ userLocalStore.getLoggedInUser().getEmail());
            txLogedINName.setText("User Name: "+ userLocalStore.getLoggedInUser().getName());
            txLogedINUUID.setText("User UUID: "+ userLocalStore.getLoggedInUser().getPassword());
            txLogedINProvider.setText("User Provider: "+ userLocalStore.getLoggedInUser().getProvider());
            txLogedINToken.setText("User Token: "+ userLocalStore.getLoggedInUser().getUserToken());

        }

        trinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PushNotification(MainActivity.this,userLocalStore.getLoggedInUser()).sendTrinkNotification();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!authenticate()) {
            goToLogin();
        }
    }

    private boolean authenticate() {
       FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();
         if(firebaseUser!= null){
             userLocalStore.setUserLoggedIn(true);
             FirebaseUser user = firebaseAuth.getCurrentUser();
             FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                 @Override
                 public void onComplete(@NonNull Task<String> task) {
                     userTocken= task.getResult();
                     userLocalStore.storeLogintype(getLogedInTyp());
                     userLocalStore.storeUserData(new User(user.getDisplayName(),user.getEmail(),user.getEmail(),-1,user.getUid(),getLogedInTyp().toString(),null,userTocken));
                 }
             });

         }
        return userLocalStore.isUserLoggedIn();
    }


    private void goToLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void logSentFriendRequestEvent() {

        logger.logEvent("sentFriendRequestNiat");
    }


    private LoginType getLogedInTyp(){
        LoginType loginType= LoginType.email;
        String provider =firebaseAuth.getCurrentUser().getProviderData().get(1).getProviderId();
        if(provider.contains("facebook")){
            loginType=  LoginType.facebook;
        }
        if(provider.contains("google")){
            loginType= LoginType.google;;
        }
        if(provider.contains("email")){
            loginType=  LoginType.email;
        }
        return loginType;
    }

    private void showDialogBox(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(text);
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }

    private void chancelNotification(){

        if(getIntent().hasExtra(getString(R.string.NOTIFICATION_ID_KEY_FRIEND_REQUEST))){
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(getIntent().getIntExtra(getString(R.string.NOTIFICATION_ID_KEY_FRIEND_REQUEST),0));

        }
        if(getIntent().hasExtra(getString(R.string.NOTIFICATION_ID_KEY_TRINK_REQUEST))){
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(getIntent().getIntExtra(getString(R.string.NOTIFICATION_ID_KEY_TRINK_REQUEST),0));

        }

    }

}