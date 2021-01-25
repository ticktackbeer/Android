package com.example.halloworld.DesignV1.Email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.halloworld.DesignV1.Anmeldeauswahl;
import com.example.halloworld.DesignV1.Utility.Helper;
import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class EmailVerification extends AppCompatActivity {
    TextView veriText;
    Button emailAnmeldeAendernBtn;
    //Button emailZurueksetzenBtn;
    UserLocalStore userLocalStore;
    String email;
    Boolean isCancelByUser=false;
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
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            Intent intent = new Intent(EmailVerification.this, EmailAnmeldung.class);
            startActivity(intent);
            finish();
        }
        String text = "Eine E-Mail wurde an "+email+" gesendet.Bitte benutze den Link in der Email, um deine Adresse zu bestätigen.Kehre dann in die" +
                "App zurück.Denken Sie daran,den Spam-Ordner zu überprüfen.Hast du Probleme beim Einloggen? Kontaktiere uns";
        veriText.setText(text);
       // emailZurueksetzenBtn = findViewById(R.id.button_zurueksetzen);
        emailAnmeldeAendernBtn = findViewById(R.id.button_anmeldemethode);

        emailAnmeldeAendernBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            isCancelByUser=true;
                            Toast.makeText(EmailVerification.this, "Ein Fehler ist aufgetreten bitte versuche es nocheinmal", Toast.LENGTH_SHORT).show();
                            Helper.logout(EmailVerification.this);
                            Intent intent = new Intent(EmailVerification.this, Anmeldeauswahl.class);
                            startActivity(intent);
                            finish();
                        }else{
                            isCancelByUser=true;
                            FirebaseDatabase.getInstance().getReference("User").child(Helper.generateEmailkey(email)).removeValue();
                            Helper.logout(EmailVerification.this);
                            Intent intent = new Intent(EmailVerification.this, Anmeldeauswahl.class);
                            startActivity(intent);
                            finish();

                        }
                    }
                });

            }
        });
        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){

                if(isCancelByUser){
                    isCancelByUser=true;
                    Helper.logout(EmailVerification.this);
                    Intent intent = new Intent(EmailVerification.this, Anmeldeauswahl.class);
                    startActivity(intent);
                    finish();
                }
                else if(!user.isEmailVerified()){
                    user.reload();
                    Toast.makeText(EmailVerification.this, "here", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(this, delay);
                }else if (user.isEmailVerified()){
                    isCancelByUser=true;
                    Helper.logout(EmailVerification.this);
                    Intent intent = new Intent(EmailVerification.this, EmailAnmeldung.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                    finish();


                }else {
                    isCancelByUser=true;
                    Helper.logout(EmailVerification.this);
                    Intent intent = new Intent(EmailVerification.this, Anmeldeauswahl.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, delay);
    }
}