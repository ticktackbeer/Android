package com.example.halloworld.DesignV1.Email;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.halloworld.DesignV1.Anmeldeauswahl;
import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailVerification extends AppCompatActivity {
    TextView veriText;
    Button emailAnmeldeAendernBtn;
    //Button emailZurueksetzenBtn;
    UserLocalStore userLocalStore;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        userLocalStore= new UserLocalStore(this);
        veriText = findViewById(R.id.EmailVeriText);
        if(userLocalStore.isUserLoggedIn()){
            email= userLocalStore.getLoggedInUser().getEmail();
        }else{
            email= getIntent().getStringExtra("email");
        }

        String text = "Eine E-Mail wurde an "+email+" gesendet.Bitte benutze den Link in der Email, um deine Adresse zu bestätigen.Kehre dann in die" +
                "App zurück.Denken Sie daran,den Spam-Ordner zu überprüfen.Hast du Probleme beim Einloggen? Kontaktiere uns";
        veriText.setText(text);
       // emailZurueksetzenBtn = findViewById(R.id.button_zurueksetzen);
        emailAnmeldeAendernBtn = findViewById(R.id.button_anmeldemethode);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        emailAnmeldeAendernBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(EmailVerification.this, "Ein Fehler ist aufgetreten bitte versuche es nocheinmal", Toast.LENGTH_SHORT).show();
                            quickLogoutVerficationFail();
                        }else{
                            quickLogout();
                        }
                    }
                });

            }
        });

    }

    public void quickLogout(){

        FirebaseDatabase.getInstance().getReference("User").child(generateEmailkey(email)).removeValue();
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(EmailVerification.this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
        LoginManager.getInstance().logOut();
        userLocalStore.clearUserData();
        Intent intent = new Intent(EmailVerification.this, Anmeldeauswahl.class);
        startActivity(intent);
        finish();
    }

    public void quickLogoutVerficationFail(){

        FirebaseAuth.getInstance().signOut();
        userLocalStore.clearUserData();
        Intent intent = new Intent(EmailVerification.this, Anmeldeauswahl.class);
        startActivity(intent);
        finish();
    }
    public String generateEmailkey(String email){

        return email.replace(".","&");
    }
}