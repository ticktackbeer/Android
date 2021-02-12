package com.example.halloworld.DesignV1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.halloworld.R;

public class Wilkommen extends AppCompatActivity {

    Button fortsetzen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wilkommen);

//      Clickable Textpart DATENSCHUTZ UND NUTZUNGSBEDINGUNGEN
        TextView textView = findViewById(R.id.text_AGB);

        String text = "Indem du auf Fortsetzen drückst, stimmst du unseren Datenschutz und Nutzungsbedingungen zu!";

        SpannableString ss = new SpannableString(text);

        // Farbe
        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(Color.YELLOW);
        // Klick
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Toast.makeText(Wilkommen.this, "Click on Link", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.YELLOW);
            }
        };
        ss.setSpan(clickableSpan,52,87, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //ss.setSpan(fcsBlue,52,87, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setHighlightColor(Color.TRANSPARENT);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


//      Button Fortsetzen
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