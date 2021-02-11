package com.example.halloworld.DesignV1;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.halloworld.R;
import com.example.halloworld.Utility.UserLocalStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeScreen extends NavigationMenu {

    // Floating Action Button
    FloatingActionButton fab_add,fab_edit,fab_image;
    Animation FabOpen,FabClose,FabRClockwise,FabRAntiClockwise;
    boolean isOpen = false;
    ImageView trinkBtn;
    UserLocalStore userLocalStore;


    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //Button homeBtnBeer = findViewById(R.id.btn_Trinken);
        toolbar= findViewById(R.id.toolbar);
        super.setDrawerLayout(this,toolbar,R.id.nav_home_screen);

        userLocalStore = new UserLocalStore(this);

        trinkBtn = findViewById(R.id.btn_Trinken);

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


/*        trinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 HelperDB.getUserFromFriendsListByEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail(), new FirebaseCallback() {
                     @Override
                     public void onCallBackFriendsList(ArrayList<User> friendList) {
                         new PushNotificationSenderService(HomeScreen.this,userLocalStore.getLoggedInUser()).sendTrinkNotification(friendList);
                     }
                 });

            }
        });*/
    }

    public void startAsyncTask(View v) {
        ExampleAsyncTask task=new ExampleAsyncTask();
        task.execute(2);

    }
    private class ExampleAsyncTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... integers) {
            ArrayList<Integer> picIds= new ArrayList<>();
            picIds.add(R.drawable.button_probe_halb);
            picIds.add(R.drawable.button_probe_voll);
            picIds.add(R.drawable.button_probe_halb);

            //picIds.add(R.drawable.bier_leer);

            ArrayList<Integer> newArray= new ArrayList<>();


            for (int i=0; i<integers[0];i++) {
                publishProgress(picIds.get(i));
                try {
                        if(i<2){
                            Thread.sleep(1000);

                        }else {
                            Thread.sleep(5000);
                        }

                }
                catch (InterruptedException e){
                    e.printStackTrace();}
            }
            return "finished";
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(HomeScreen.this, s, Toast.LENGTH_SHORT).show();
            trinkBtn.setBackground(getResources().getDrawable(R.drawable.button_probe_leer));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            trinkBtn.setBackground(getResources().getDrawable(values[0]));
        }
    }
}