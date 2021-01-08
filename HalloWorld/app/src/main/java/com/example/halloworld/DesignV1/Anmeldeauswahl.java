package com.example.halloworld.DesignV1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.halloworld.EmailLogin;
import com.example.halloworld.EmailRegistration;
import com.example.halloworld.Enum.LoginType;
import com.example.halloworld.Login;
import com.example.halloworld.MainActivity;
import com.example.halloworld.Model.User;
import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Anmeldeauswahl extends AppCompatActivity {

    UserLocalStore userLocalStore;
    FirebaseAuth firebaseAuth;
    String userTocken;


    //google
    SignInButton btnGlogin;
    GoogleSignInClient googleSignInClient;

    //facebook
    CallbackManager callbackManager;
    LoginButton btnFlogin;
    static final String FTag = "FacebookAuthentication";
    String facebookrequestedEmail;
    String infoText;

    // Email
    Button btnElogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anmeldeauswahl);
        userLocalStore = new UserLocalStore(this);

        firebaseAuth = FirebaseAuth.getInstance();
        btnElogin = findViewById(R.id.button_Email);
        btnGlogin = findViewById(R.id.button_Google);
        btnFlogin = findViewById(R.id.button_Facebook);
        btnFlogin.setReadPermissions("email", "public_profile");
        quickLogout();
        Glogin();
        facebookLogin();
        EmailLogin();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (userLocalStore.getLoginTyp() == LoginType.google) {
            if (requestCode == 100) {
                // when request code is equel to 100
                Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

                if (signInAccountTask.isSuccessful()) {

                    try {
                        //Initialize sign account
                        GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                        //Check condtion
                        if (googleSignInAccount != null) {

                            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

                            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //Chek condtion
                                    if (task.isSuccessful()) {
                                        userLocalStore.setUserLoggedIn(true);
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        userLocalStore.storeLogintype(LoginType.google);

                                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                userTocken= task.getResult();
                                                User person = new User(user.getDisplayName(), user.getEmail(), user.getEmail(), 1, user.getUid(), LoginType.google.toString(),"null",userTocken);
                                                userLocalStore.storeUserData(person);
                                                try {
                                                    FirebaseDatabase.getInstance().getReference().child("User").child(generateEmailkey(user.getEmail())).setValue(person);
                                                }catch (Exception e){
                                                    showDialogBox(e.getMessage());
                                                }
                                                // Redirect to profile activty
                                                Intent intent = new Intent(Anmeldeauswahl.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });



                                    } else {
                                        showDialogBox(task.getException().getMessage());
                                        Intent intent = new Intent(Anmeldeauswahl.this, Anmeldeauswahl.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });

                        }
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (userLocalStore.getLoginTyp() == LoginType.facebook) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }


    }

    private void Glogin() {

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("675714750172-viipjd5hq67r468p3tu8prrf0jso0275.apps.googleusercontent.com")
                .requestEmail()
                .build();
        //getString(R.string.default_web_client_id)
        googleSignInClient = GoogleSignIn.getClient(Anmeldeauswahl.this, googleSignInOptions);

        btnGlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLocalStore.storeLogintype(LoginType.google);
                if (firebaseAuth.getCurrentUser() != null) {
                    quickLogout();
                }
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });

    }

    private void facebookLogin() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        btnFlogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                        if (object.has("email")) {
                            try {
                                String email = object.getString("email");

                                facebookrequestedEmail = email;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();

                Log.d(FTag, "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());

            }
            @Override
            public void onCancel() {
                userLocalStore.storeLogintype(LoginType.facebook);
                Log.d(FTag, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                userLocalStore.storeLogintype(LoginType.facebook);
                Log.d(FTag, "onError");
            }
        });


        btnFlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLocalStore.storeLogintype(LoginType.facebook);
            }
        });

    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(Anmeldeauswahl.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userLocalStore.setUserLoggedIn(true);
                    userLocalStore.storeLogintype(LoginType.facebook);
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            userTocken= task.getResult();
                            User person = new User(user.getDisplayName(), user.getEmail(), user.getEmail(), -1, user.getUid(), LoginType.facebook.toString(),null,userTocken);

                            userLocalStore.storeUserData(person );
                            FirebaseDatabase.getInstance().getReference("User").child(generateEmailkey(user.getEmail())).setValue(person);

                            Intent intent = new Intent(Anmeldeauswahl.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                } else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException) {

                    FirebaseAuthUserCollisionException exception =
                            (FirebaseAuthUserCollisionException) task.getException();
                    if (exception.getErrorCode() ==
                            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL") {
                        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(facebookrequestedEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.isSuccessful()) {
                                   if (task.getResult().getSignInMethods().get(0).contains("google")) {
                                        infoText= "Du bist bereits mit einem Google Account registriert.Bitte melde dich mit Google an.";
                                        googleLoginForFacebook(infoText);
                                    } else if (task.getResult().getSignInMethods().get(0).contains("email")) {
                                        infoText= "Du bist bereits mit deiner Email Adresse registriert.Bitte melde dich mit deiner Email Adresse an.";
                                        googleLoginForEmail(infoText);
                                    } else {
                                        infoText="User hat keinen Provider";
                                        showDialogBox(infoText);
                                    }

                                }else{
                                    infoText="Account bei einem anderem Provider?";
                                    showDialogBox(task.getException().getMessage());
                                }
                                LoginManager.getInstance().logOut();
                            }
                        });
                    } else {
                        showDialogBox(task.getException().getMessage());
                    }
                    LoginManager.getInstance().logOut();
                }else{
                    showDialogBox(task.getException().getMessage());
                    LoginManager.getInstance().logOut();
                }
            }
        });
    }

    private void EmailLogin() {
        btnElogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLocalStore.storeLogintype(LoginType.email);
                if (firebaseAuth.getCurrentUser() != null) {
                    quickLogout();
                }
                Intent intent = new Intent(Anmeldeauswahl.this, EmailAnmeldung.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }


    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void showDialogBox(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Anmeldeauswahl.this);
        dialogBuilder.setMessage(text);
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();

    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            Log.d("TAG", "Error parsing JSON");
        }
        return null;
    }

    public void googleLoginForFacebook(String infoText){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Anmeldeauswahl.this);
        dialogBuilder.setMessage(infoText);
        dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                ).requestIdToken("675714750172-viipjd5hq67r468p3tu8prrf0jso0275.apps.googleusercontent.com")
                        .requestEmail()
                        .build();
                googleSignInClient = GoogleSignIn.getClient(Anmeldeauswahl.this, googleSignInOptions);

                userLocalStore.storeLogintype(LoginType.google);
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });
        dialogBuilder.show();

    }

    public void googleLoginForEmail(String infoText){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Anmeldeauswahl.this);
        dialogBuilder.setMessage(infoText);
        dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userLocalStore.storeLogintype(LoginType.email);
                if (firebaseAuth.getCurrentUser() != null) {
                    quickLogout();

                }
                Intent intent = new Intent(Anmeldeauswahl.this, EmailLogin.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialogBuilder.show();

    }

    public void quickLogout(){
        firebaseAuth = FirebaseAuth.getInstance();
        googleSignInClient = GoogleSignIn.getClient(Anmeldeauswahl.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        googleSignInClient.signOut();
        firebaseAuth.signOut();
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
}