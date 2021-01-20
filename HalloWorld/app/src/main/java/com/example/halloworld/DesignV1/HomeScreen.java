package com.example.halloworld.DesignV1;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.example.halloworld.DesignV1.Email.EmailAnmeldung;
import com.example.halloworld.DesignV1.Email.EmailStartScreen;
import com.example.halloworld.DesignV1.Service.PushNotificationSenderService;
import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeScreen extends NavigationMenu {

    // Floating Action Button
    FloatingActionButton fab_add,fab_edit,fab_image;
    Animation FabOpen,FabClose,FabRClockwise,FabRAntiClockwise;
    boolean isOpen = false;
    ImageButton trinkBtn;
    UserLocalStore userLocalStore;


    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        toolbar= findViewById(R.id.toolbar);
        super.setDrawerLayout(this,toolbar,R.id.nav_home_screen);

        userLocalStore = new UserLocalStore(this);
        trinkBtn = findViewById(R.id.imagebtn_Trinken);

        // Floating Action Button im HomeScreen
        fab_add=findViewById(R.id.add_btn);
        fab_edit=findViewById(R.id.edit_btn);
        fab_image=findViewById(R.id.image_btn);
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.from_bottom_anim);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.to_bottom_anim);
        FabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_close_anim);
        FabRAntiClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_close_anim);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpen) {
                    fab_image.startAnimation(FabClose);
                    fab_edit.startAnimation(FabClose);
                    fab_add.startAnimation(FabRAntiClockwise);
                    fab_edit.setClickable(false);
                    fab_image.setClickable(false);
                    isOpen = false;
                } else {
                    fab_image.startAnimation(FabOpen);
                    fab_edit.startAnimation(FabOpen);
                    fab_add.startAnimation(FabRClockwise);
                    fab_edit.setClickable(true);
                    fab_image.setClickable(true);
                    isOpen = true;
                }
            }
        });

        // Floating Action Button - Freund hinzufügen
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, FreundHinzufuegen.class);
                startActivity(intent);
            }
        });

        // Floating Action Button - Freundes Liste
        fab_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, FreundesListe.class);
                startActivity(intent);
            }
        });


        trinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PushNotificationSenderService(HomeScreen.this,userLocalStore.getLoggedInUser()).sendTrinkNotification();
            }
        });
    }
}