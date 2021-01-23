package com.example.halloworld.DesignV1.Service;

import android.content.Context;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushNotificationSenderService {

    Context context;
    User userSender;
    User userReciver;
    RequestQueue requestQueue;
    static String URL = "https://fcm.googleapis.com/fcm/send";



    public PushNotificationSenderService(Context context,User userSender, User userReciver) {
        this.context = context;
        this.userSender = userSender;
        this.userReciver = userReciver;
        requestQueue = Volley.newRequestQueue(context);
    }

    public PushNotificationSenderService( Context context,User userSender) {
        this.context = context;
        this.userSender = userSender;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void sendTrinkNotification() {
        String newtitel;
        String newbody;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        newtitel = "Du musst aufholen!";
        newbody = "Dein Buddy " + userSender.getEmail() + " hat etwas getrunken";


        JSONObject json = new JSONObject();
        try {

            json.put("to", "/topics/" + userSender.getEmail().replace("@", "AT"));

            JSONObject dataObjUser = new JSONObject();
            dataObjUser.put("name", userSender.getName());
            dataObjUser.put("nickname", userSender.getNickname());
            dataObjUser.put("password", userSender.getPassword());
            dataObjUser.put("provider", userSender.getProvider());
            dataObjUser.put("userName", userSender.getUserName());
            dataObjUser.put("email", userSender.getEmail());
            dataObjUser.put("userToken", userSender.getUserToken());

            JSONObject dataObj = new JSONObject();
            dataObj.put("title", newtitel);
            dataObj.put("body", newbody);
            dataObj.put("friendRequest", "false");
            dataObj.put("notificationType", NotificationType.trinkRequest);
            dataObj.put("senderEmail", userSender.getEmail());
            dataObj.put("senderToken", userSender.getUserToken());
            dataObj.put("userObjectSender",dataObjUser);

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
        newbody = "Willst du " + userSender.getEmail() + " zu deiner Freundesliste hinzufügen";

        JSONObject json = new JSONObject();
        try {
            json.put("to", userReciver.getUserToken());
            //json.put("to","/topics/"+"test");
            /*JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", newtitel);
            notificationObj.put("body", newbody);
            notificationObj.put("click_action", "MainActivity");
            */
            JSONObject dataObjUserSender = new JSONObject();
            dataObjUserSender.put("name", userSender.getName());
            dataObjUserSender.put("nickname", userSender.getNickname());
            dataObjUserSender.put("password", userSender.getPassword());
            dataObjUserSender.put("provider", userSender.getProvider());
            dataObjUserSender.put("userName", userSender.getUserName());
            dataObjUserSender.put("email", userSender.getEmail());
            dataObjUserSender.put("userToken", userSender.getUserToken());

            JSONObject dataObjUserResponse = new JSONObject();
            dataObjUserResponse.put("name", userReciver.getName());
            dataObjUserResponse.put("nickname", userReciver.getNickname());
            dataObjUserResponse.put("password", userReciver.getPassword());
            dataObjUserResponse.put("provider", userReciver.getProvider());
            dataObjUserResponse.put("userName", userReciver.getUserName());
            dataObjUserResponse.put("email", userReciver.getEmail());
            dataObjUserResponse.put("userToken", userReciver.getUserToken());

            JSONObject dataObj = new JSONObject();
            dataObj.put("title", newtitel);
            dataObj.put("body", newbody);
            dataObj.put("notificationType", NotificationType.friendRequest);
            dataObj.put("userObjectSender",dataObjUserSender);
            dataObj.put("userObjectReciver",dataObjUserResponse);

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
        newbody = userSender.getEmail() + " hat deinen Freundschafstantrag angenommen";

        JSONObject json = new JSONObject();
        try {
            json.put("to", userSender.getUserToken());

            JSONObject dataObjUsersender = new JSONObject();
            dataObjUsersender.put("name", userSender.getName());
            dataObjUsersender.put("nickname", userSender.getNickname());
            dataObjUsersender.put("password", userSender.getPassword());
            dataObjUsersender.put("provider", userSender.getProvider());
            dataObjUsersender.put("userName", userSender.getUserName());
            dataObjUsersender.put("email", userSender.getEmail());
            dataObjUsersender.put("userToken", userSender.getUserToken());

            JSONObject dataObjUserResponse = new JSONObject();
            dataObjUserResponse.put("name", userReciver.getName());
            dataObjUserResponse.put("nickname", userReciver.getNickname());
            dataObjUserResponse.put("password", userReciver.getPassword());
            dataObjUserResponse.put("provider", userReciver.getProvider());
            dataObjUserResponse.put("userName", userReciver.getUserName());
            dataObjUserResponse.put("email", userReciver.getEmail());
            dataObjUserResponse.put("userToken", userReciver.getUserToken());

            JSONObject dataObj = new JSONObject();
            dataObj.put("title", newtitel);
            dataObj.put("body", newbody);
            dataObj.put("notificationType", NotificationType.friendRequestResponse);
            dataObj.put("userObjectSender",dataObjUsersender);
            dataObj.put("userObjectReciver",dataObjUserResponse);

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

        Log.i("sendTrinkNotification", "trink: " + userSender.getEmail());

        newtitel = "Neue Nachricht von "+ userSender.getEmail();
        newbody = body;


        JSONObject json = new JSONObject();
        try {

            json.put("to", userSender.getUserToken());


            JSONObject dataObjUsersender = new JSONObject();
            dataObjUsersender.put("name", userSender.getName());
            dataObjUsersender.put("nickname", userSender.getNickname());
            dataObjUsersender.put("password", userSender.getPassword());
            dataObjUsersender.put("provider", userSender.getProvider());
            dataObjUsersender.put("userName", userSender.getUserName());
            dataObjUsersender.put("email", userSender.getEmail());
            dataObjUsersender.put("userToken", userSender.getUserToken());

            JSONObject dataObj = new JSONObject();
            dataObj.put("title", newtitel);
            dataObj.put("body", newbody);
            dataObj.put("friendRequest", "false");
            dataObj.put("notificationType", NotificationType.trinkReply);
            dataObj.put("userObjectSender",dataObjUsersender);

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
