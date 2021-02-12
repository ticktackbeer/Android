package com.example.halloworld.DesignV1.Utility;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.halloworld.DesignV1.Interface.FirebaseCallback;
import com.example.halloworld.Model.User;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Helper extends AppCompatActivity {

   public static void logout(Context context){

        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
        FirebaseAuth.getInstance().signOut();
        UserLocalStore userLocalStore = new UserLocalStore(context);
        userLocalStore.clearUserData();
        LoginManager.getInstance().logOut();
    }

   public static String generateEmailkey(String email){

       return email.replace(".","&");
    }



}
