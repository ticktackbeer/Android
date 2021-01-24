package com.example.halloworld.DesignV1;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.halloworld.Model.User;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Helper extends AppCompatActivity {
    static ArrayList<String> friendsStringArrayList;
    static ArrayList<User> friendsUserArrayList;
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

    public static ArrayList<User> getUserFromFriendsListByEmail(String email){

        DatabaseReference databaseReferenceFriend= FirebaseDatabase.getInstance().getReference().child("Friend").child(Helper.generateEmailkey(email));

        if(databaseReferenceFriend!=null){
            databaseReferenceFriend.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        friendsStringArrayList = new ArrayList<>();
                        for (DataSnapshot item: snapshot.getChildren()) {
                            String user = item.getValue().toString();
                            friendsStringArrayList.add(user);

                        }

                        DatabaseReference databaseReferenceUser= FirebaseDatabase.getInstance().getReference().child("User");
                        if(databaseReferenceUser!=null){
                            databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        friendsUserArrayList = new ArrayList<>();
                                        for (DataSnapshot item: snapshot.getChildren()) {
                                            User user = item.getValue(User.class);
                                            if (friendsStringArrayList.contains(user.getEmail())){
                                                friendsUserArrayList.add(user);
                                            }
                                        }


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    //Toast.makeText(FreundHinzufuegen.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }



                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //Toast.makeText(PushNotificationSenderService.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        return friendsUserArrayList;
    }
}
