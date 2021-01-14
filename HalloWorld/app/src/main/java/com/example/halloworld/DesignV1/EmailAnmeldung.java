package com.example.halloworld.DesignV1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.halloworld.EmailLogin;
import com.example.halloworld.Enum.LoginType;
import com.example.halloworld.MainActivity;
import com.example.halloworld.Model.User;
import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.login.LoginManager;
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

public class EmailAnmeldung extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText email;
    EditText password;
    Button btnConfirm;
    UserLocalStore userLocalStore;
    String userTocken;
    TextView registration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_anmeldung);

        userLocalStore = new UserLocalStore(this);
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.AnmeldungEmail);
        password = findViewById(R.id.RegistrationPassword);
        btnConfirm = findViewById(R.id.button_Bestätigen);
        registration = findViewById(R.id.textRegistrieren);

        String textRegistration = (String) registration.getText();

        SpannableString ss = new SpannableString(textRegistration);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(EmailAnmeldung.this, Registrierung.class);
                startActivity(intent);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.orange));
            }
        };

        ss.setSpan(clickableSpan,textRegistration.indexOf("Dann"),textRegistration.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registration.setText(ss);
        registration.setMovementMethod(LinkMovementMethod.getInstance());
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = email.getText().toString();
                String inputPassword = password.getText().toString();
                firebaseAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
                        .addOnCompleteListener(EmailAnmeldung.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    userLocalStore.setUserLoggedIn(true);
                                    FirebaseUser user = firebaseAuth.getCurrentUser();

                                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            userTocken= task.getResult();
                                            User person = new User(user.getDisplayName(), user.getEmail(), user.getEmail(), -1, user.getUid(), LoginType.email.toString(),null,userTocken);
                                            userLocalStore.storeUserData(person );
                                            FirebaseDatabase.getInstance().getReference("User").child("User").child(generateEmailkey(user.getEmail())).setValue(person);
                                            // Redirect to profile activty
                                            Intent intent = new Intent(EmailAnmeldung.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    });

                                } else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException) {

                                    // User mit account verlinken
                                } else if(task.getException() instanceof FirebaseAuthInvalidUserException){
                                    FirebaseAuthInvalidUserException ee= (FirebaseAuthInvalidUserException) task.getException();
                                    showErrorMessage(ee.getMessage());
                                }else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                    FirebaseAuthInvalidCredentialsException ee= (FirebaseAuthInvalidCredentialsException) task.getException();
                                    showErrorMessage(ee.getMessage());
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Exception ex = task.getException();
                                    showErrorMessage(ex.getMessage());
                                }

                            }
                        });
            }
        });
    }
    public String generateEmailkey(String email){

        return email.replace(".","&");
    }

    private void showErrorMessage(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailAnmeldung.this);
        dialogBuilder.setMessage(text);
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }
}