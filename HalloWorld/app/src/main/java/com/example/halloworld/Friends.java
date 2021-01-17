package com.example.halloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.halloworld.Adapter.AdapterClassFriends;
import com.example.halloworld.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Friends extends AppCompatActivity implements AdapterClassFriends.FriendsClickInterface {
    DatabaseReference databaseReference;
    ArrayList<String> userArrayList;
    RecyclerView recyclerView;
    //SearchView searchView;
    private Toolbar toolbar;
   // Button friendRequest;
    //ArrayList<String> currentUserList;
    User myUserInfo;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Friend").child(generateEmailkey(user.getEmail()));
        recyclerView = findViewById(R.id.rv);
        //searchView= findViewById(R.id.searchview);
        //searchView.setIconifiedByDefault(false);
        //friendRequest=findViewById(R.id.SearchfriendRequestBtn);
        swipeRefreshLayout= findViewById(R.id.swipeLayout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(Friends.this, "refresh", Toast.LENGTH_SHORT).show();
                refreshList();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(databaseReference!=null){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        userArrayList = new ArrayList<>();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        for (DataSnapshot item: snapshot.getChildren()) {
                            String user = item.getValue().toString();
                            userArrayList.add(user);
                            /*if(user.getEmail().equals(firebaseUser.getEmail())){
                                myUserInfo=user;
                            }*/
                        }
                        Log.i("user NIIIIAAAATTTT","Size Liste "+ userArrayList.toString());
                        AdapterClassFriends adapterClass = new AdapterClassFriends(userArrayList,Friends.this);
                        recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Friends.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
/*        if(searchView!=null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    friendRequest.setEnabled(false);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //enable button is on onSearchbarClickListener methode
                    friendRequest.setEnabled(false);
                    search(newText.trim());
                    return true;
                }
            });
        }*/
    }

    public void refreshList(){
        if(databaseReference!=null){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        userArrayList = new ArrayList<>();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        for (DataSnapshot item: snapshot.getChildren()) {
                            String user = item.getValue().toString();
                            userArrayList.add(user);
                            /*if(user.getEmail().equals(firebaseUser.getEmail())){
                                myUserInfo=user;
                            }*/
                        }
                        Log.i("user NIIIIAAAATTTT","Size Liste "+ userArrayList.toString());
                        AdapterClassFriends adapterClass = new AdapterClassFriends(userArrayList,Friends.this);
                        recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Friends.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

/*    private void search(String s){
        currentUserList = new ArrayList<>();
        if(!s.isEmpty()){
            for (String item:userArrayList) {
                if(item.toLowerCase().contains(s.toLowerCase())){
                    currentUserList.add(item);
                }
            }
            AdapterClassFriends adapterClass = new AdapterClassFriends(currentUserList,this);
            recyclerView.setAdapter(adapterClass);
        }else{
            currentUserList= new ArrayList<>();
            AdapterClassFriends adapterClass = new AdapterClassFriends(currentUserList,this);
            recyclerView.setAdapter(adapterClass);
        }

    }*/

    @Override
    public void onFriendsClickListener(int position) {
        // hier das machen was ich will / in sarchbar schreiben
        //userArrayList.get(position);
        //String user = currentUserList.get(position);
        //searchView.setQuery(user.getEmail(),false);
        Toast.makeText(this, "position: "+position, Toast.LENGTH_SHORT).show();

    }

    public String generateEmailkey(String email){
        /*String senderResponseEmailkey;
        String senderResponseEmailFirstPart;
        String senderResponseEmailLastPart;
        int index2 =email.lastIndexOf(".");
        senderResponseEmailLastPart= email.substring(index2).replace(".","&");
        senderResponseEmailFirstPart=email.substring(0,index2);;
        senderResponseEmailkey = senderResponseEmailFirstPart+senderResponseEmailLastPart;
        return senderResponseEmailkey;*/
        return email.replace(".","&");
    }
}