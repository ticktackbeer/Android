package com.example.halloworld.DesignV1.Email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.halloworld.DesignV1.Anmeldeauswahl;
import com.example.halloworld.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class EmailStartScreen extends AppCompatActivity {
    EditText inputEmail;
    Button buttonBestaetigung;
    String infoText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_start_screen);

        inputEmail = findViewById(R.id.input_Email);
        buttonBestaetigung = findViewById(R.id.button_Bestätigen);

        buttonBestaetigung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                if(!email.isEmpty() || email.contains("@")){

                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().getSignInMethods().isEmpty()){
                                    Intent intent = new Intent(EmailStartScreen.this, EmailRegistrierung.class)
                                            .putExtra("email",email);
                                    startActivity(intent);
                                    finish();
                                }
                                  String provider = task.getResult().getSignInMethods().get(0);
                                if (provider.contains("google")) {
                                    infoText= "Du bist mit deiner Email Adresse "+email+" bereits mit einem Google Account registriert.Bitte melde dich mit Google an.";
                                    showDialogBoxRedirectAnmeldung(infoText);
                                } else if (provider.contains("facebook")) {
                                    infoText= "Du bist mit deiner Email Adresse "+email+" bereits mit einem Facebook Account registriert.Bitte melde dich mit Facebook an.";
                                    showDialogBoxRedirectAnmeldung(infoText);
                                }else if(provider.contains("password")){
                                    Intent intent = new Intent(EmailStartScreen.this, EmailAnmeldung.class)
                                            .putExtra("email",email);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                        }
                    });
                }else{
                    // keine email angegeben oder ungültig
                }
            }
        });
    }

    private void showDialogBoxRedirectAnmeldung(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailStartScreen.this);
        dialogBuilder.setMessage(text);
        dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(EmailStartScreen.this, Anmeldeauswahl.class);
                startActivity(intent);
                finish();
            }
        });
        dialogBuilder.show();

    }

    private void showDialogBox(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailStartScreen.this);
        dialogBuilder.setMessage(text);
        dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(EmailStartScreen.this, Anmeldeauswahl.class);
//                startActivity(intent);
//                finish();
            }
        });
        dialogBuilder.show();

    }
}