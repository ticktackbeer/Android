package com.example.halloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.halloworld.Adapter.AdapterClassFriends;
import com.example.halloworld.Utility.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FreundesListe extends NavigationMenu implements AdapterClassFriends.FriendsClickInterface {
    DatabaseReference databaseReference;
    ArrayList<String> userArrayList;
    RecyclerView recyclerView;
    private Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freundes_liste);
        toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle("");
        super.setDrawerLayout(this,toolbar,R.id.nav_freundesliste);

        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Friend").child(Helper.generateEmailkey(user.getEmail()));
        recyclerView = findViewById(R.id.rv);
        swipeRefreshLayout= findViewById(R.id.swipeLayout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(FreundesListe.this, "refresh", Toast.LENGTH_SHORT).show();
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
                            if(user.equals(firebaseUser.getEmail())){
                                continue;
                            }
                            userArrayList.add(user);

                        }
                        Log.i("user NIIIIAAAATTTT","Size Liste "+ userArrayList.toString());
                        AdapterClassFriends adapterClass = new AdapterClassFriends(userArrayList,FreundesListe.this);
                        recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(FreundesListe.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onFriendsClickListener(int position) {
        // hier das machen was ich will / in sarchbar schreiben
        //userArrayList.get(position);
        //String user = currentUserList.get(position);
        //searchView.setQuery(user.getEmail(),false);
        Toast.makeText(this, "position: "+position, Toast.LENGTH_SHORT).show();

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
                            if(user.equals(firebaseUser.getEmail())){
                                continue;
                            }
                            userArrayList.add(user);
                        }
                        Log.i("user NIIIIAAAATTTT","Size Liste "+ userArrayList.toString());
                        AdapterClassFriends adapterClass = new AdapterClassFriends(userArrayList,FreundesListe.this);
                        recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(FreundesListe.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}