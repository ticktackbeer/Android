package com.example.halloworld.DesignV1;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

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
