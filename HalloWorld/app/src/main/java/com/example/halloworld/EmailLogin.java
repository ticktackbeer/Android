package com.example.halloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.halloworld.Enum.LoginType;
import com.example.halloworld.Interface.GetUserCallback;
import com.example.halloworld.Model.User;
import com.example.halloworld.Utility.SqlManager;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.Arrays;

public class EmailLogin extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button btnElogin;
    EditText email;
    EditText password;
    static final String ETag = "EmailAuthentication";
    UserLocalStore userLocalStore;
    String infoText;
    GoogleSignInClient googleSignInClient;
    CallbackManager callbackmanager;
    String userTocken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emaillogin);

        firebaseAuth = FirebaseAuth.getInstance();
        btnElogin = findViewById(R.id.loginBtn);
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);

        userLocalStore = new UserLocalStore(this);


        btnElogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = email.getText().toString();
                String inputPassword = password.getText().toString();
               // User person = new User(inputEmail, inputPassword);
                firebaseAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
                        .addOnCompleteListener(EmailLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(ETag, "signInWithEmail:success");
                                    Toast.makeText(EmailLogin.this, "Email SignIn User Authentication successful", Toast.LENGTH_SHORT).show();
                                    userLocalStore.setUserLoggedIn(true);
                                    FirebaseUser user = firebaseAuth.getCurrentUser();

                                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            userTocken= task.getResult();
                                        }
                                    });
                                    User person = new User(user.getDisplayName(), user.getEmail(), user.getEmail(), -1, user.getUid(), LoginType.email.toString(),null,userTocken);
                                    userLocalStore.storeUserData(person );
                                    FirebaseDatabase.getInstance().getReference("User").child("User").child(generateEmailkey(user.getEmail())).setValue(person);
                                    // Redirect to profile activty
                                    Intent intent = new Intent(EmailLogin.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException) {

                                    FirebaseAuthUserCollisionException exception =
                                            (FirebaseAuthUserCollisionException) task.getException();
                                    if (exception.getErrorCode() ==
                                            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL") {
                                        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(inputEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                                if (task.isSuccessful()) {
                                                    if (task.getResult().getSignInMethods().get(0).contains("facebook")) {
                                                        infoText = "Du bist bereits mit einem Facebook Account registriert.Bitte melde dich mit Google an.";
                                                        facebookLoginForEmail(infoText);
                                                    } else if (task.getResult().getSignInMethods().get(0).contains("google")) {
                                                        infoText = "Du bist bereits mit einem Google Account registriert.Bitte melde dich mit Google an.";
                                                        googleLoginForEmail(infoText);
                                                    } else if (task.getResult().getSignInMethods().get(0).contains("email")) {
                                                        // darf nicht passieren --> er meldet sich mit Email an und hat ein Email Login bei Firebase
                                                        //infoText= "Du bist bereits mit deiner Email Adresse registriert.Bitte melde dich mit deiner Email Adresse an.";
                                                        // googleLoginForEmail(infoText);
                                                    } else {
                                                        infoText = "User hat keinen Provider";
                                                        showErrorMessage(infoText);
                                                    }

                                                } else {
                                                    infoText = "Account bei einem anderem Provider?";
                                                    showErrorMessage(infoText);
                                                }
                                                LoginManager.getInstance().logOut();
                                            }
                                        });
                                    } else {
                                        Log.d("FTag", "signin with credential: failed");
                                        showErrorMessage("firebase Exception");
                                    }
                                    LoginManager.getInstance().logOut();
                                } else if(task.getException() instanceof FirebaseAuthInvalidUserException){
                                    FirebaseAuthInvalidUserException ee= (FirebaseAuthInvalidUserException) task.getException();
                                    showErrorMessage(ee.getMessage());
                                }else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                    FirebaseAuthInvalidCredentialsException ee= (FirebaseAuthInvalidCredentialsException) task.getException();
                                    showErrorMessage(ee.getMessage());
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(ETag, "signInWithEmail:failure", task.getException());
                                    Exception ex = task.getException();
                                    showErrorMessage(ex.getMessage());
                                }

                            }
                        });
            }
        });

    }

    private void authenticate(User user) {
        SqlManager manager = new SqlManager(this);
        manager.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(Boolean isSuccessful, User returnedUser) {
                if (isSuccessful) {
                    logUserIn(returnedUser);
                } else {
                    showErrorMessage();
                }
            }
        });
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailLogin.this);
        dialogBuilder.setMessage("User not found.Please Sign up.");
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }

    private void showErrorMessage(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailLogin.this);
        dialogBuilder.setMessage(text);
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }

    private void logUserIn(User returnedUser) {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
        Toast.makeText(EmailLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EmailLogin.this, MainActivity.class);
        startActivity(intent);
    }

    public void googleLoginForEmail(String infoText) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailLogin.this);
        dialogBuilder.setMessage(infoText);
        dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                ).requestIdToken("675714750172-viipjd5hq67r468p3tu8prrf0jso0275.apps.googleusercontent.com")
                        .requestEmail()
                        .build();
                googleSignInClient = GoogleSignIn.getClient(EmailLogin.this, googleSignInOptions);

                userLocalStore.storeLogintype(LoginType.google);
                if (firebaseAuth.getCurrentUser() != null) {
                    quickLogout();
                }
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });
        dialogBuilder.show();

    }

    public void facebookLoginForEmail(String infoText) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailLogin.this);
        dialogBuilder.setMessage(infoText);
        dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userLocalStore.storeLogintype(LoginType.email);
                if (firebaseAuth.getCurrentUser() != null) {
                    quickLogout();

                }
            facebookrequeet();
            }
        });
        dialogBuilder.show();

    }

    public void facebookrequeet() {

        callbackmanager = CallbackManager.Factory.create();

        //   Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email, user_birthday,user_friends"));

        LoginManager.getInstance().registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                Log.d("LoginActivity", response.toString());
                                Log.d("LoginActivity", object.toString());

                                Intent home = new Intent(EmailLogin.this, Login.class);
                                startActivity(home);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {

                Log.v("LoginActivity", exception.getCause().toString());

            }
        });


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

    public void quickLogout(){
        Log.i("TAG", "quickLogout: delete token ");
        firebaseAuth = FirebaseAuth.getInstance();
        Log.i("TAG", "onComplete: delete token ");
        googleSignInClient = GoogleSignIn.getClient(EmailLogin.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        Log.i("TAG", "onComplete2: quickLogout in Login delete token ");
        googleSignInClient.signOut();
        firebaseAuth.signOut();
        userLocalStore.clearUserData();
        LoginManager.getInstance().logOut();
        Log.i("TAG", "quickLogout Login: "+ "hat geklappt");

    }

}