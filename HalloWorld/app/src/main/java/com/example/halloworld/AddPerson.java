 package com.example.halloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.halloworld.Interface.GetUserCallback;
import com.example.halloworld.Model.User;
import com.example.halloworld.Utility.SqlManager;
import com.example.halloworld.Utility.UserLocalStore;

 public class AddPerson extends AppCompatActivity {

    String Name;
    String email;
    String username;
    int age;
    Button button;
    EditText nameInput;
    EditText emailInput;
    EditText userNameInput;
    EditText ageInput;
    User user;
    UserLocalStore userLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        nameInput = (EditText) findViewById(R.id.addPersonName);
        emailInput = (EditText) findViewById(R.id.addPersonEmail);
        userNameInput= (EditText) findViewById(R.id.addPersonUsername);
        ageInput= (EditText) findViewById(R.id.addPersonAge);
        button = (Button) findViewById(R.id.addPersonbutton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name= nameInput.getText().toString();
                email =emailInput.getText().toString();
                username = userNameInput.getText().toString();
                age = Integer.valueOf(ageInput.getText().toString());

                user = new User();
                user.setName(Name);
                user.setUserName(username);
                user.setEmail(email);
                user.setAge(age);
                savePerson(user);
                userLocalStore.storeUserData(user);
                userLocalStore.setUserLoggedIn(true);
            }
        });
        userLocalStore = new UserLocalStore(this);
    }

    public void savePerson(User userItem){

        Toast.makeText(AddPerson.this, userItem.getUserName(), Toast.LENGTH_SHORT).show();
        Toast.makeText(AddPerson.this, userItem.getName(), Toast.LENGTH_SHORT).show();
        Toast.makeText(AddPerson.this, userItem.getEmail(), Toast.LENGTH_SHORT).show();
        Toast.makeText(AddPerson.this,String.valueOf(userItem.getAge()), Toast.LENGTH_SHORT).show();


        SqlManager SqlHelper= new SqlManager(this);
        SqlHelper.storeUserDataInBackground(userItem, new GetUserCallback() {
            @Override
            public void done(Boolean issuccessful, User returnedUser) {
                // nothing to do ??
            }
        });

    }

}