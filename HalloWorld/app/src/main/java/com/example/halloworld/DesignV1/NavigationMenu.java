package com.example.halloworld.DesignV1;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.halloworld.Enum.LoginType;
import com.example.halloworld.Model.User;
import com.example.halloworld.PushNotification;
import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class NavigationMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Activity activity;
    int nav_id;
    UserLocalStore userLocalStore;

    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    public void setDrawerLayout(Activity activity, Toolbar toolbar, int nav_id) {

        this.activity = activity;
        this.nav_id = nav_id;
        drawerLayout = findViewById(R.id.navigation_layout);
        navigationView = findViewById(R.id.navigation_view);

        toolbar.setTitle("");
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

        userLocalStore = new UserLocalStore(this);

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.navigationheaderemail)).setText(userLocalStore.getLoggedInUser().getEmail());
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.navigationheadername)).setText(userLocalStore.getLoggedInUser().getNickname());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_karte:
                if (nav_id != R.id.nav_maps) {
                    Intent intent = new Intent(this, Karte.class);
                    startActivity(intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_ausloggen:
                quickLogout();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_pushtest:
                Intent intent = new Intent(this, PushNotification.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_profil:
                Intent intent1 = new Intent(this, Profil.class);
                startActivity(intent1);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_freundhinzufuegen:
//                Intent intent2 = new Intent(this, SearchBar.c
//                lass);
                Intent intent2 = new Intent(this, FreundHinzufuegen.class);
                startActivity(intent2);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_home_screen:
                Intent intent3 = new Intent(this, HomeScreen.class);
                startActivity(intent3);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_freundesliste:
                Intent intent4 = new Intent(this, FreundesListe.class);
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

        firebaseAuth = FirebaseAuth.getInstance();
        googleSignInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
        googleSignInClient.signOut();
        LoginManager.getInstance().logOut();
        firebaseAuth.signOut();
        userLocalStore.clearUserData();
        Intent intent = new Intent(activity, Anmeldeauswahl.class);
        startActivity(intent);
        finish();
    }
}
