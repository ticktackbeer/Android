package com.example.halloworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.halloworld.Email.EmailAnmeldung;
import com.example.halloworld.Email.EmailStartScreen;
import com.example.halloworld.Utility.Helper;
import com.example.halloworld.Enum.LoginType;
import com.example.halloworld.Model.User;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import java.util.Arrays;


public class Anmeldeauswahl extends AppCompatActivity {

    UserLocalStore userLocalStore;
    FirebaseAuth firebaseAuth;
    String userTocken;
    String nickname;

   AuthCredential FBAuthCredential;
    //google
    Button btnGlogin;
    GoogleSignInClient googleSignInClient;

    //facebook
    CallbackManager callbackManager;
    Button btnFlogin;
//    LoginButton btnFlogin;
    static final String FTag = "FacebookAuthentication";
    String facebookrequestedEmail;
    String infoText;

    // Email
    Button btnElogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anmeldeauswahl);

//------Clickable Textpart Kontakt Salud Amigos-----------------------------------------------------

        TextView textView = findViewById(R.id.text_Kontakt);
        String text = "Hast du Probleme beim Einloggen? Salud Amigos kontaktieren";

        SpannableString ss = new SpannableString(text);

        // Farbe
        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(Color.YELLOW);
        // Klick
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Toast.makeText(Anmeldeauswahl.this, "Click on Link", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.YELLOW);

            }
        };
        ss.setSpan(clickableSpan,33,58, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setHighlightColor(Color.TRANSPARENT);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

//--------------------------------------------------------------------------------------------------

        userLocalStore = new UserLocalStore(this);

        firebaseAuth = FirebaseAuth.getInstance();
        btnElogin = findViewById(R.id.button_Email);
        btnGlogin = findViewById(R.id.button_Google);
        btnFlogin = findViewById(R.id.button_Facebook);
        Helper.logout(this);
        Glogin();
        facebookLogin();
        EmailLogin();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (userLocalStore.getLoginTyp() == LoginType.google) {

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
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        userLocalStore.storeLogintype(LoginType.google);
                                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                userTocken= task.getResult();
                                                SaveUserAndRedirectToHome(requestCode,user,LoginType.google);
//                                                User person = new User(user.getDisplayName(), user.getEmail(), user.getEmail(), 1, user.getUid(), LoginType.google.toString(),"null",userTocken);
//                                                userLocalStore.storeUserData(person);
//                                                try {
//                                                    FirebaseDatabase.getInstance().getReference().child("User").child(generateEmailkey(user.getEmail())).setValue(person);
//                                                }catch (Exception e){
//                                                    showDialogBox(e.getMessage());
//                                                }
//                                                if(requestCode==200){
//                                                    user.linkWithCredential(FBAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<AuthResult> task) {
//                                                            // Redirect to profile activty
//                                                            Intent intent = new Intent(Anmeldeauswahl.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                            startActivity(intent);
//                                                            finish();
//                                                        }
//                                                    });
//                                                }else {
//                                                    // Redirect to profile activty
//                                                    Intent intent = new Intent(Anmeldeauswahl.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
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
                    Helper.logout(Anmeldeauswahl.this);
                }
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });

    }

    private void facebookLogin() {
       //wird nicht mehr benötigt neues SDK-- FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        btnFlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLocalStore.storeLogintype(LoginType.facebook);
                LoginManager.getInstance().logInWithReadPermissions(Anmeldeauswahl.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

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
            }
        });

    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(Anmeldeauswahl.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userLocalStore.storeLogintype(LoginType.facebook);
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            userTocken= task.getResult();

                            SaveUserAndRedirectToHome(100,user,LoginType.facebook);
//                            User person = new User(user.getDisplayName(), user.getEmail(), user.getEmail(), -1, user.getUid(), LoginType.facebook.toString(),null,userTocken);
//
//                            userLocalStore.storeUserData(person );
//                            FirebaseDatabase.getInstance().getReference("User").child(generateEmailkey(user.getEmail())).setValue(person);
//
//                            Intent intent = new Intent(Anmeldeauswahl.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
                        }
                    });

                } else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException) {

                    FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) task.getException();
                    if (exception.getErrorCode() ==
                            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL") {

                        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(facebookrequestedEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.isSuccessful()) {
                                   if (task.getResult().getSignInMethods().get(0).contains("google")) {
                                        infoText= "Du bist bereits mit einem Google Account registriert.Um deinen Account zu verlinken drücke 'OK'";
                                       FBAuthCredential = credential;
                                        googleLoginForFacebook(infoText,facebookrequestedEmail);
                                    } else if (task.getResult().getSignInMethods().get(0).contains("password")) {
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
                Intent intent = new Intent(Anmeldeauswahl.this, EmailStartScreen.class);
                startActivity(intent);

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

    public void googleLoginForFacebook(String infoText, String FBEmail){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Anmeldeauswahl.this);
        dialogBuilder.setMessage(infoText);
        dialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Anmeldeauswahl.this, Anmeldeauswahl.class);
                startActivity(intent);
                finish();
            }
        });
        dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                ).requestIdToken("675714750172-viipjd5hq67r468p3tu8prrf0jso0275.apps.googleusercontent.com")
                        .setAccountName(FBEmail)
                        .build();
                googleSignInClient = GoogleSignIn.getClient(Anmeldeauswahl.this, googleSignInOptions);

                userLocalStore.storeLogintype(LoginType.google);


                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 200);

            }
        });
        dialogBuilder.show();

    }

    public void googleLoginForEmail(String infoText){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Anmeldeauswahl.this);
        dialogBuilder.setMessage(infoText);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userLocalStore.storeLogintype(LoginType.email);
                if (firebaseAuth.getCurrentUser() != null) {
                    Helper.logout(Anmeldeauswahl.this);

                }
                Intent intent = new Intent(Anmeldeauswahl.this, EmailAnmeldung.class)
                        .putExtra("email",facebookrequestedEmail);
                startActivity(intent);
                finish();
            }
        });
        dialogBuilder.show();

    }


    public String SaveUserAndRedirectToHome(int requestCode,FirebaseUser user,LoginType loginType){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(Anmeldeauswahl.this);
        myDialog.setTitle("Bitte gebe deinen Spitznamen ein");

        final View customLayout = getLayoutInflater().inflate(R.layout.nickname_dialog,null);
        // Dialogbox zum abfragen des Nick Names
        final EditText nicknameInput = customLayout.findViewById(R.id.dialog_input);
        nicknameInput.setText(user.getDisplayName());
        myDialog.setView(customLayout);
        myDialog.setCancelable(false).setPositiveButton("Name Setzen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nickname=nicknameInput.getText().toString();
                User person = new User(user.getDisplayName(), user.getEmail(), user.getEmail(), 1, user.getUid(), loginType.toString(),nickname,userTocken);
                userLocalStore.storeUserData(person);
                userLocalStore.setUserLoggedIn(true);
                try {
                    FirebaseDatabase.getInstance().getReference().child("User").child(Helper.generateEmailkey(user.getEmail())).setValue(person);
                }catch (Exception e){
                    showDialogBox(e.getMessage());
                }
                FirebaseMessaging.getInstance().subscribeToTopic("saludamigos");
                if(requestCode==200){
                    user.linkWithCredential(FBAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Redirect to profile activty
                            Intent intent = new Intent(Anmeldeauswahl.this, HomeScreen.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else {
                    // Redirect to profile activty
                    Intent intent = new Intent(Anmeldeauswahl.this, HomeScreen.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        AlertDialog alert = myDialog.create();
        alert.show();
        return nickname;
    }


}