package com.example.halloworld;

import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.halloworld.Enum.NotificationType;
import com.example.halloworld.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushNotification extends DrawerMenu {
    Button button;
    Button S10button;
    Button Huawaibutton;
    Button Tabletbutton;
    EditText titel;
    EditText body;
    String URL = "https://fcm.googleapis.com/fcm/send";
    private Toolbar toolbar;
    String friendTokenTest;
    String friendToken;
    RequestQueue requestQueue;
    Context context;
    User userSenderRequestResponse;
    User userSenderRequest;


 /*   public PushNotification(Context context) {
        this.context = context;
    }*/

    public PushNotification(User userSenderRequest, User userSenderRequestResponse, Context context) {
        this.context = context;
        this.userSenderRequest = userSenderRequest;
        this.userSenderRequestResponse = userSenderRequestResponse;
    }

    public PushNotification( Context context,User userSenderRequest) {
        this.context = context;
        this.userSenderRequest = userSenderRequest;
    }

    public PushNotification() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Push Test");
        super.setDrawerLayout(this, toolbar, R.id.nav_pushtest);
        S10button = findViewById(R.id.BtnNotifiIdS10);
        Huawaibutton = findViewById(R.id.BtnNotifiIdHuawai);
        Tabletbutton = findViewById(R.id.BtnNotifiIdtablet);
        button = findViewById(R.id.BtnNotifiId);
        titel = findViewById(R.id.TitelNotifiId);
        body = findViewById(R.id.BodyNotifiId);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        requestQueue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("test");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firebaseUser != null) {
                    context= PushNotification.this;
                    sendTrinkNotification();
                }

            }
        });
        S10button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    friendTokenTest = "dTh3_sKOTAOSikN3bZxlt5:APA91bHGP_gpj4gwofuYHaFESuU8ZDGiWI45OX2Q9M06WdWGyhs7ecg-Rr-Moy1pQeilFZij43YNTfSrG3xzQR7M5Su-cFLPHRpWb5QNmWvubBdB0arJyHMUD_XSoYpEDdJDNq88mvX5";
                    context= PushNotification.this;
                    sendFriendRequestNotification();
                }

            }
        });

        Huawaibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    friendTokenTest = "co46JM5PRZGg05kDhXCkkJ:APA91bFwjr6Iz4jO6dEEudvG0hwndbsyXDYLqjbcKzlSGelOxTGCoF3POoAa2KvnpGbb8d2-NSHOQ4FxymgXY4KGe-0DB-Rv8_a0BZ9R1DLj8SrCXQTuEihXTwR52E84Bmk6jbRNuPNX";
                    context= PushNotification.this;
                    sendFriendRequestNotification();
                }

            }
        });

        Tabletbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    friendTokenTest = "co46JM5PRZGg05kDhXCkkJ:APA91bFwjr6Iz4jO6dEEudvG0hwndbsyXDYLqjbcKzlSGelOxTGCoF3POoAa2KvnpGbb8d2-NSHOQ4FxymgXY4KGe-0DB-Rv8_a0BZ9R1DLj8SrCXQTuEihXTwR52E84Bmk6jbRNuPNX";
                    context= PushNotification.this;
                    sendFriendRequestNotification();
                }

            }
        });

    }


    public void sendTrinkNotification() {
        String newtitel;
        String newbody;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (userSenderRequest.getEmail() == null) {

            newtitel = titel.getText().toString();
            newbody = body.getText().toString();
        } else {
            newtitel = "Du musst aufholen!";
            newbody = "Dein Buddy " + userSenderRequest.getEmail() + " hat etwas getrunken";
        }

        JSONObject json = new JSONObject();
        try {
            if (userSenderRequest.getEmail() == null) {
                json.put("to", "/topics/" + "test");
            } else {
                json.put("to", "/topics/" + userSenderRequest.getEmail().replace("@", "AT"));
            }

            JSONObject dataObjUser = new JSONObject();
            dataObjUser.put("name", userSenderRequest.getName());
            dataObjUser.put("nickname", userSenderRequest.getNickname());
            dataObjUser.put("password", userSenderRequest.getPassword());
            dataObjUser.put("provider", userSenderRequest.getProvider());
            dataObjUser.put("userName", userSenderRequest.getUserName());
            dataObjUser.put("email", userSenderRequest.getEmail());
            dataObjUser.put("userToken", userSenderRequest.getUserToken());

            JSONObject dataObj = new JSONObject();
            dataObj.put("title", newtitel);
            dataObj.put("body", newbody);
            dataObj.put("friendRequest", "false");
            dataObj.put("notificationType", NotificationType.trinkRequest);
            dataObj.put("senderEmail", userSenderRequest.getEmail());
            dataObj.put("senderToken", userSenderRequest.getUserToken());
            dataObj.put("userObjectSender",dataObjUser);



            /*dataObj.put("senderResponseEmail", userSenderRequestResponse.getEmail());
            dataObj.put("senderResponseToken", userSenderRequestResponse.getUserToken());*/

            //json.put("notification", notificationObj);
            json.put("data", dataObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.i("MUR", "onResponse: ");
                            Toast.makeText(context, "Push successful", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("MUR", "onError: " + error.networkResponse);
                    Toast.makeText(context, "Push failed", Toast.LENGTH_SHORT).show();
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAnVO81tw:APA91bG6c-WsIF4RGITXXodf3JcXthwAavzfu-YmSYu-LdxWa-r9DyvpZrXAKkq04z5IFv8rxcQz1RGECRqbTsFzPQiy7579_1EgmZTbe93RPH41pXjFrWe3Qn6eB9Enz5V6Gr8hZALb");
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendFriendRequestNotification() {
        String newtitel;
        String newbody;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        newtitel = "Neue Freundschaftsanfrage";
        newbody = "Willst du " + userSenderRequest.getEmail() + " zu deiner Freundesliste hinzufügen";

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
        JSONObject json = new JSONObject();
        try {
            json.put("to", userSenderRequestResponse.getUserToken());
            //json.put("to","/topics/"+"test");
            /*JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", newtitel);
            notificationObj.put("body", newbody);
            notificationObj.put("click_action", "MainActivity");
            */
            JSONObject dataObjUserSender = new JSONObject();
            dataObjUserSender.put("name", userSenderRequest.getName());
            dataObjUserSender.put("nickname", userSenderRequest.getNickname());
            dataObjUserSender.put("password", userSenderRequest.getPassword());
            dataObjUserSender.put("provider", userSenderRequest.getProvider());
            dataObjUserSender.put("userName", userSenderRequest.getUserName());
            dataObjUserSender.put("email", userSenderRequest.getEmail());
            dataObjUserSender.put("userToken", userSenderRequest.getUserToken());

            JSONObject dataObjUserResponse = new JSONObject();
            dataObjUserResponse.put("name", userSenderRequestResponse.getName());
            dataObjUserResponse.put("nickname", userSenderRequestResponse.getNickname());
            dataObjUserResponse.put("password", userSenderRequestResponse.getPassword());
            dataObjUserResponse.put("provider", userSenderRequestResponse.getProvider());
            dataObjUserResponse.put("userName", userSenderRequestResponse.getUserName());
            dataObjUserResponse.put("email", userSenderRequestResponse.getEmail());
            dataObjUserResponse.put("userToken", userSenderRequestResponse.getUserToken());

            JSONObject dataObj = new JSONObject();
            dataObj.put("title", newtitel);
            dataObj.put("body", newbody);
            dataObj.put("friendRequest", "true");
            dataObj.put("senderEmail", userSenderRequest.getEmail());
            dataObj.put("senderResponseEmail", userSenderRequestResponse.getEmail());
            dataObj.put("notificationType", NotificationType.friendRequest);
            dataObj.put("senderToken", userSenderRequest.getUserToken());
            dataObj.put("senderResponseToken", userSenderRequestResponse.getUserToken());
            dataObj.put("userObjectSender",dataObjUserSender);
            dataObj.put("userObjectResponse",dataObjUserResponse);

            //json.put("notification", notificationObj);
            json.put("data", dataObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.i("MUR", "onResponse: ");
                            Toast.makeText(context, "Push successful", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("MUR", "onError: " + error.networkResponse);
                    Toast.makeText(context, "Push failed", Toast.LENGTH_SHORT).show();
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAnVO81tw:APA91bG6c-WsIF4RGITXXodf3JcXthwAavzfu-YmSYu-LdxWa-r9DyvpZrXAKkq04z5IFv8rxcQz1RGECRqbTsFzPQiy7579_1EgmZTbe93RPH41pXjFrWe3Qn6eB9Enz5V6Gr8hZALb");
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendFriendRequestResponseNotification() {
        String newtitel;
        String newbody;
        Log.i("MUR", "notificationType: sendFriendRequestResponseNotification");
        requestQueue = Volley.newRequestQueue(context);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        newtitel = "Neuer Buddy !!";
        newbody = userSenderRequest.getEmail() + " hat deinen Freundschafstantrag angenommen";
        //getUserToken("Email des empfänger");
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
        JSONObject json = new JSONObject();
        try {
            json.put("to", userSenderRequest.getUserToken());
            //json.put("to","/topics/"+"test");
            /*JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", newtitel);
            notificationObj.put("body", newbody);*/

            JSONObject dataObjUsersender = new JSONObject();
            dataObjUsersender.put("name", userSenderRequest.getName());
            dataObjUsersender.put("nickname", userSenderRequest.getNickname());
            dataObjUsersender.put("password", userSenderRequest.getPassword());
            dataObjUsersender.put("provider", userSenderRequest.getProvider());
            dataObjUsersender.put("userName", userSenderRequest.getUserName());
            dataObjUsersender.put("email", userSenderRequest.getEmail());
            dataObjUsersender.put("userToken", userSenderRequest.getUserToken());

            JSONObject dataObjUserResponse = new JSONObject();
            dataObjUserResponse.put("name", userSenderRequestResponse.getName());
            dataObjUserResponse.put("nickname", userSenderRequestResponse.getNickname());
            dataObjUserResponse.put("password", userSenderRequestResponse.getPassword());
            dataObjUserResponse.put("provider", userSenderRequestResponse.getProvider());
            dataObjUserResponse.put("userName", userSenderRequestResponse.getUserName());
            dataObjUserResponse.put("email", userSenderRequestResponse.getEmail());
            dataObjUserResponse.put("userToken", userSenderRequestResponse.getUserToken());

            JSONObject dataObj = new JSONObject();
            dataObj.put("title", newtitel);
            dataObj.put("body", newbody);
            dataObj.put("friendRequest", "false");
            //dataObj.put("senderEmail", userSenderRequest.getEmail());
            //dataObj.put("senderResponseEmail", emailResponse);
            dataObj.put("notificationType", NotificationType.friendRequestResponse);
            //dataObj.put("senderToken", "not needed");
            dataObj.put("userObjectSender",dataObjUsersender);
            dataObj.put("userObjectResponse",dataObjUserResponse);

            //json.put("notification", notificationObj);
            json.put("data", dataObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.i("MUR", "onResponse: ");
                            Toast.makeText(context, "Push successful", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("MUR", "onError: " + error.networkResponse);
                    Toast.makeText(context, "Push failed", Toast.LENGTH_SHORT).show();
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAnVO81tw:APA91bG6c-WsIF4RGITXXodf3JcXthwAavzfu-YmSYu-LdxWa-r9DyvpZrXAKkq04z5IFv8rxcQz1RGECRqbTsFzPQiy7579_1EgmZTbe93RPH41pXjFrWe3Qn6eB9Enz5V6Gr8hZALb");
                    return header;
                }
            };
            Log.i("MUR", "notificationType: sendFriendRequestResponseNotification2");
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendTrinkReplyNotification(String body) {
        String newtitel;
        String newbody;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("sendTrinkNotification", "trink: " + userSenderRequest.getEmail());

            newtitel = "Neue Nachricht von "+ userSenderRequest.getEmail();
            newbody = body;


        JSONObject json = new JSONObject();
        try {

            json.put("to", userSenderRequest.getUserToken());

            /*JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", newtitel);
            notificationObj.put("body", newbody);*/

            JSONObject dataObjUsersender = new JSONObject();
            dataObjUsersender.put("name", userSenderRequest.getName());
            dataObjUsersender.put("nickname", userSenderRequest.getNickname());
            dataObjUsersender.put("password", userSenderRequest.getPassword());
            dataObjUsersender.put("provider", userSenderRequest.getProvider());
            dataObjUsersender.put("userName", userSenderRequest.getUserName());
            dataObjUsersender.put("email", userSenderRequest.getEmail());
            dataObjUsersender.put("userToken", userSenderRequest.getUserToken());

            JSONObject dataObj = new JSONObject();
            dataObj.put("title", newtitel);
            dataObj.put("body", newbody);
            dataObj.put("friendRequest", "false");
            dataObj.put("notificationType", NotificationType.trinkReply);
            dataObj.put("userObjectSender",dataObjUsersender);

            /*dataObj.put("senderResponseEmail", userSenderRequestResponse.getEmail());
            dataObj.put("senderResponseToken", userSenderRequestResponse.getUserToken());*/

            //json.put("notification", notificationObj);
            json.put("data", dataObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.i("MUR", "onResponse: ");
                            Toast.makeText(context, "Push successful", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("MUR", "onError: " + error.networkResponse);
                    Toast.makeText(context, "Push failed", Toast.LENGTH_SHORT).show();
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAnVO81tw:APA91bG6c-WsIF4RGITXXodf3JcXthwAavzfu-YmSYu-LdxWa-r9DyvpZrXAKkq04z5IFv8rxcQz1RGECRqbTsFzPQiy7579_1EgmZTbe93RPH41pXjFrWe3Qn6eB9Enz5V6Gr8hZALb");
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}