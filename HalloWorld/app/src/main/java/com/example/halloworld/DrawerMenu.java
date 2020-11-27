package com.example.halloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.halloworld.Enum.LoginType;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class DrawerMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Activity activity;
    int nav_id;
    UserLocalStore userLocalStore;

    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setDrawerLayout(Activity activity, Toolbar toolbar, int nav_id) {

        this.activity = activity;
        this.nav_id = nav_id;
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.activity, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        if (nav_id != 0) {
            navigationView.setCheckedItem(nav_id);
        }

        userLocalStore = new UserLocalStore(this.activity);

        if (userLocalStore.isUserLoggedIn()) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_login).setVisible(false);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_maps:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new BlankFragment()).commit();
                if (nav_id != R.id.nav_maps) {
                    Intent intent = new Intent(this, GoogleMaps.class);
                    startActivity(intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_logout:
                //logout(activity);
                quickLogout();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_pushtest:
                Intent intent = new Intent(this, PushNotification.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_profile:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_person_add:
                Intent intent2 = new Intent(this,SearchBar.class);
                startActivity(intent2);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_home:
                Intent intent3 = new Intent(this, MainActivity.class);
                startActivity(intent3);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_people:
                Intent intent4 = new Intent(this,Friends.class);
                startActivity(intent4);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;



        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    public void logout(Activity activity) {

        firebaseAuth = FirebaseAuth.getInstance();
        userLocalStore = new UserLocalStore(activity);
        userLocalStore.storeLogintype(getLogedInTyp());
        this.activity=activity;

            //Logout Google
            if (userLocalStore.getLoginTyp() == LoginType.google) {
                //Sign out from google
                googleSignInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            firebaseAuth.signOut();
                            userLocalStore.setUserLoggedIn(false);
                            userLocalStore.clearUserData();
                            showDialogBox("Google Logout successful");

                            Intent intent = new Intent(activity, Login.class);
                            startActivity(intent);
                        } else {
                            showDialogBox(task.getException().getMessage());
                        }
                    }
                });
            }

            //Logout facebook
            if (userLocalStore.getLoginTyp() == LoginType.facebook) {

                firebaseAuth.signOut();
                userLocalStore.setUserLoggedIn(false);
                userLocalStore.clearUserData();
                LoginManager.getInstance().logOut();
                showDialogBox("Facebook Logout DrawerMenu successful");
                //finsh activity
                Intent intent = new Intent(activity, Login.class);
                startActivity(intent);
            }
            //Logout email
            if (userLocalStore.getLoginTyp() == LoginType.email) {


                FirebaseUser user = firebaseAuth.getCurrentUser();
                firebaseAuth.signOut();
                userLocalStore.setUserLoggedIn(false);
                userLocalStore.clearUserData();
                showDialogBox("Email Logout successful");
                //finsh activity
                Intent intent = new Intent(activity, Login.class);
                startActivity(intent);
            }

    }

    private void showDialogBox(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setMessage(text);
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }

    private LoginType getLogedInTyp() {
        LoginType loginType = LoginType.email;
        String provider = firebaseAuth.getCurrentUser().getProviderData().get(1).getProviderId();
        if (provider.contains("facebook")) {
            loginType = LoginType.facebook;
        }
        if (provider.contains("google")) {
            loginType = LoginType.google;
            ;
        }
        if (provider.contains("email")) {
            loginType = LoginType.email;
        }
        return loginType;
    }

   public void quickLogout(){

       FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               firebaseAuth = FirebaseAuth.getInstance();
               googleSignInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
               googleSignInClient.signOut();
               firebaseAuth.signOut();
               userLocalStore.clearUserData();
               LoginManager.getInstance().logOut();
               Intent intent = new Intent(activity, Login.class);
               startActivity(intent);
           }
       });

   }

}

