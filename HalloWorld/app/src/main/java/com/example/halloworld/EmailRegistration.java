package com.example.halloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.halloworld.Enum.LoginType;
import com.example.halloworld.Enum.UserState;
import com.example.halloworld.Interface.GetUserCallback;
import com.example.halloworld.Model.User;
import com.example.halloworld.Utility.SqlManager;
import com.example.halloworld.Utility.UserLocalStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class EmailRegistration extends AppCompatActivity {

    private Button button;
    private EditText inputeMail;
    private EditText inputPassword;
    //private EditText inputUserName;
    //private EditText inputAge;
    //private EditText inputName;
    UserLocalStore userLocalStore;
    FirebaseAuth firebaseAuth;
    static final String ETag = "EmailAuthentication";
    String userTocken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailregistration);

        userLocalStore = new UserLocalStore(this);
        firebaseAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.RegistartionBtn);
        inputeMail = findViewById(R.id.RegistrationEmail);
        inputPassword = findViewById(R.id.RegistrationPassword);
        //inputUserName= findViewById(R.id.RegistrationUserName);
        //inputAge= findViewById(R.id.RegistrationAge);
        //inputName = findViewById(R.id.RegistrationName);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String name= inputName.getText().toString();
                String email = inputeMail.getText().toString();
                //String username = inputUserName.getText().toString();
                String password = inputPassword.getText().toString();
                //int age = Integer.valueOf(inputAge.getText().toString());
                //User user = new User(name, username, email, age, password);
                //registerUser(user);
                registerInFirebase();
            }
        });

    }

    private void registerInFirebase() {
        firebaseAuth.createUserWithEmailAndPassword(inputeMail.getText().toString(), inputPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ETag, "signInWithEmail:success");
                            Toast.makeText(EmailRegistration.this, "Email create User Authentication successful", Toast.LENGTH_SHORT).show();
                            userLocalStore.setUserLoggedIn(true);
                            userLocalStore.storeLogintype(LoginType.email);
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    userTocken= task.getResult();
                                }
                            });
                            User person = new User(user.getDisplayName(), user.getEmail(), user.getEmail(), -1, user.getUid(), LoginType.email.toString(),null,userTocken);

                            userLocalStore.storeUserData(person );
                            FirebaseDatabase.getInstance().getReference("User").child(generateEmailkey(user.getEmail())).setValue(person);

                            // Redirect to profile activty
                            Intent intent = new Intent(EmailRegistration.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ETag, "signInWithEmail:failure", task.getException());
                            showErrorMessage(task.getException().getMessage());
                            //*Toast.makeText(EmailLogin.this, "Email create User Authentication failed.",Toast.LENGTH_SHORT).show();
                            //finish();
                        }

                    }
                });

    }

    private void registerUser(User user) {
        SqlManager sqlManager = new SqlManager(EmailRegistration.this);
        UserState state = sqlManager.checkUserExist(user.getEmail());
        if (state == UserState.userDoNotExist) {
            sqlManager.storeUserDataInBackground(user, new GetUserCallback() {
                @Override
                public void done(Boolean isSuccessful, User returnedUser) {
                    if (isSuccessful) {
                        Toast.makeText(EmailRegistration.this, "Registration successful,please login.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EmailRegistration.this, Login.class);
                        startActivity(intent);
                    } else {
                        registerfaild();
                    }

                }
            });
        } else if (state == UserState.userExist) {
            userExist(user.getEmail());
        } else {
            registerfaild();
        }

    }

    private void registerfaild() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailRegistration.this);
        dialogBuilder.setMessage("Register faild! Please try again.");
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }

    private void userExist(String email) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailRegistration.this);
        dialogBuilder.setMessage("User with email: " + email + " already exist!");
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }

    private void showErrorMessage(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmailRegistration.this);
        dialogBuilder.setMessage(text);
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }

    public String generateEmailkey(String email){
        String senderResponseEmailkey;
        String senderResponseEmailFirstPart;
        String senderResponseEmailLastPart;
        int index2 =email.lastIndexOf(".");
        senderResponseEmailLastPart= email.substring(index2).replace(".","&");
        senderResponseEmailFirstPart=email.substring(0,index2);;
        senderResponseEmailkey = senderResponseEmailFirstPart+senderResponseEmailLastPart;
        return senderResponseEmailkey;
    }


}