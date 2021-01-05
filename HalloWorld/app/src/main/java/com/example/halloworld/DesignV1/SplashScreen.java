package com.example.halloworld.DesignV1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.halloworld.Model.User;
import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.os.Build.VERSION.SDK_INT;

public class SplashScreen extends AppCompatActivity {

    //Variablen
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;
    private static int SPLASH_SCREEN = 3000;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        userLocalStore = new UserLocalStore(this);
        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.image_Splash);
        logo = findViewById(R.id.topic_Splash);
        slogan = findViewById(R.id.text_Motto);


        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(!userLocalStore.isUserLoggedIn()){
                    intent= new Intent(SplashScreen.this, Wilkommen.class);
                }else{
                    intent= new Intent(SplashScreen.this, HomeScreen.class);
                }


              Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logo, "logo_text");
                if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,pairs);
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, pairs);
                    startActivity(intent, options.toBundle());
                    finish();
                }
            }
        }, SPLASH_SCREEN);

    }
}