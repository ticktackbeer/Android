package com.example.halloworld.DesignV1;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.halloworld.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeScreen extends NavigationMenu {

    // Floating Action Button
    FloatingActionButton fab_add,fab_edit,fab_image;
    Animation FabOpen,FabClose,FabRClockwise,FabRAntiClockwise;
    boolean isOpen = false;


    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        toolbar= findViewById(R.id.toolbar);
        super.setDrawerLayout(this,toolbar,R.id.nav_home);

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
    }
}