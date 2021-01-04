package com.example.halloworld.DesignV1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.halloworld.R;

public class Wilkommen extends AppCompatActivity {

    Button fortsetzen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wilkommen);

        fortsetzen = findViewById(R.id.button_Fortsetzen);

        fortsetzen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wilkommen.this, Anmeldeauswahl.class);
                startActivity(intent);
                finish();
            }
        });

    }
}