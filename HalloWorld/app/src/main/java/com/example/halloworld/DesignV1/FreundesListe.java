package com.example.halloworld.DesignV1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.halloworld.Adapter.AdapterClassSearchBar;
import com.example.halloworld.Model.User;
import com.example.halloworld.PushNotification;
import com.example.halloworld.R;
import com.example.halloworld.SearchBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FreundesListe extends NavigationMenu implements AdapterClassSearchBar.SearchbarClickInterface {

    DatabaseReference databaseReference;
    ArrayList<User> userArrayList;
    RecyclerView recyclerView;
    SearchView searchView;
    private Toolbar toolbar;
    Button friendRequest;
    ArrayList<User> currentUserList;
    User myUserInfo;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freundes_liste);
        toolbar= findViewById(R.id.toolbar);
        super.setDrawerLayout(this,toolbar,R.id.nav_home);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("User");
        recyclerView = findViewById(R.id.rv);
        searchView= findViewById(R.id.searchview);
        searchView.setIconifiedByDefault(false);
        friendRequest=findViewById(R.id.SearchfriendRequestBtn);
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
                            User user = item.getValue(User.class);
                            userArrayList.add(user);
                            if(user.getEmail().equals(firebaseUser.getEmail())){
                                myUserInfo=user;
                            }
                        }
                        Log.i("user NIIIIAAAATTTT","Size Liste "+ userArrayList.toString());
                        //AdapterClassSearchBar adapterClass = new AdapterClassSearchBar(userArrayList);
                        //recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(FreundesListe.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(searchView!=null){
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
        }
    }

    @Override
    public void onSearchbarClickListener(int position) {
        // hier das machen was ich will / in sarchbar schreiben
        //userArrayList.get(position);
        User user = currentUserList.get(position);
        searchView.setQuery(user.getEmail(),false);
        Toast.makeText(this, "position: "+position, Toast.LENGTH_SHORT).show();
        friendRequest.setEnabled(true);

        friendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PushNotification(myUserInfo,user,FreundesListe.this).sendFriendRequestNotification();
            }
        });


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
                            User user = item.getValue(User.class);
                            userArrayList.add(user);
                            if(user.getEmail().equals(firebaseUser.getEmail())){
                                myUserInfo=user;
                            }
                        }
                        Log.i("user NIIIIAAAATTTT","Size Liste "+ userArrayList.toString());
                        //AdapterClassSearchBar adapterClass = new AdapterClassSearchBar(userArrayList);
                        //recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(FreundesListe.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void search(String s){
        currentUserList = new ArrayList<>();
        if(!s.isEmpty()){
            for (User item:userArrayList) {
                if(item.getEmail().toLowerCase().contains(s.toLowerCase())){
                    currentUserList.add(item);
                }
            }
            AdapterClassSearchBar adapterClassSearchBar = new AdapterClassSearchBar(currentUserList,this);
            recyclerView.setAdapter(adapterClassSearchBar);
        }else{
            currentUserList= new ArrayList<>();
            AdapterClassSearchBar adapterClassSearchBar = new AdapterClassSearchBar(currentUserList,this);
            recyclerView.setAdapter(adapterClassSearchBar);
        }

    }
}