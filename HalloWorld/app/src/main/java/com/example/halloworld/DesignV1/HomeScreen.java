package com.example.halloworld.DesignV1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.halloworld.DrawerMenu;
import com.example.halloworld.R;

public class HomeScreen extends DrawerMenu {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        super.setDrawerLayout(this,toolbar,R.id.nav_home);
    }
}