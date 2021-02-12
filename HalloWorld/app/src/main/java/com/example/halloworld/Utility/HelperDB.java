package com.example.halloworld.Utility;

import androidx.annotation.NonNull;

import com.example.halloworld.Interface.FirebaseCallback;
import com.example.halloworld.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class HelperDB {
    static ArrayList<String> friendsStringArrayList;
    static ArrayList<User> friendsUserArrayList;
    public static void removeUserFromFriendRequestList(String userSenderEmail, String userReciverEmail){

        FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(Helper.generateEmailkey(userSenderEmail)).child("Gesendet").child(Helper.generateEmailkey(userReciverEmail)).removeValue();
        FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(Helper.generateEmailkey(userReciverEmail)).child("Empfangen").child(Helper.generateEmailkey(userSenderEmail)).removeValue();
    }

    public static void saveUserInFriend(User user, User friend){

        FirebaseDatabase.getInstance().getReference("Friend").child(Helper.generateEmailkey(user.getEmail())).child(Helper.generateEmailkey(friend.getEmail())).setValue(friend.getEmail());

    }



    public static void getUserFromFriendsListByEmail(String email, final FirebaseCallback firebaseCallback){

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

                                        firebaseCallback.onCallBackFriendsList(friendsUserArrayList);
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
    }
}
