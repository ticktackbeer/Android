package com.example.halloworld.DesignV1.Email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.halloworld.DesignV1.Anmeldeauswahl;
import com.example.halloworld.Enum.LoginType;
import com.example.halloworld.Model.User;
import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class EmailRegistrierung extends AppCompatActivity {

    private Button button;
    private String inputeMail;
    private EditText inputPassword;
    private EditText inputName;
    private EditText inputPasswordBestaetigung;

    UserLocalStore userLocalStore;
    FirebaseAuth firebaseAuth;

    String userTocken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrierung);

        userLocalStore = new UserLocalStore(this);
        firebaseAuth = FirebaseAuth.getInstance();

        button = findViewById(R.id.button_Bestätigen);
        inputeMail = getIntent().getStringExtra("email");
        inputPasswordBestaetigung= findViewById(R.id.input_Passwortbestätigen);
        inputPassword = findViewById(R.id.input_Passwort);
        inputName = findViewById(R.id.input_Name);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputeMail;
                String userName = inputName.getText().toString();
                String password = inputPassword.getText().toString();
                String passwordBestaetigung = inputPasswordBestaetigung.getText().toString();


                if(!password.equals(passwordBestaetigung)){

                    showErrorMessage("Passwort ist nicht identisch");
                }else if(email.isEmpty()|| !email.contains("@")){
                    showErrorMessage("Email Adresse nicht korrekt");
                }else if(userName.isEmpty()){
                    showErrorMessage("Bitte überprüfe deinen Namen");
                }else{
                    registerInFirebase();
                }


            }
        });

    }



    private void registerInFirebase() {
        firebaseAuth.createUserWithEmailAndPassword(inputeMail, inputPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            userLocalStore.setUserLoggedIn(true);
                            userLocalStore.storeLogintype(LoginType.email);
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    userTocken= task.getResult();
                                    User person = new User(user.getDisplayName(), inputName.getText().toString(), user.getEmail(), -1, user.getUid(), LoginType.email.toString(),null,userTocken);
                                    userLocalStore.storeUserData(person );
                                    FirebaseDatabase.getInstance().getReference("User").child(generateEmailkey(user.getEmail())).setValue(person);

                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Zur anmeldeauswahl damit der user sich anmelden kann.
                                            Intent intent = new Intent(EmailRegistrierung.this, EmailVerification.class)
                                                    .putExtra("email",inputeMail);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showErrorMessage("Registrieung fehlgeschlagen.Bitte versuche es erneut oder kontaktiere uns.");
                                        }
                                    });

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            showErrorMessage(task.getException().getMessage());
                            //*Toast.makeText(EmailLogin.this, "Email create User Authentication failed.",Toast.LENGTH_SHORT).show();
                            //finish();
                        }

                    }
                });
    }


    public String generateEmailkey(String email){
        return email.replace(".","&");
    }

    private void showErrorMessage(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailRegistrierung.this);
        dialogBuilder.setMessage(text);
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }


}