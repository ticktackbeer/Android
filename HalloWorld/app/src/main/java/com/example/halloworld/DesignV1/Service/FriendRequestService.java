package com.example.halloworld.DesignV1.Service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.halloworld.Model.User;
import com.example.halloworld.PushNotification;
import com.example.halloworld.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class FriendRequestService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String notificationType =intent.getStringExtra("notificationType");
        User userSender = (User) intent.getSerializableExtra("userSender");
        User userReciver = (User) intent.getSerializableExtra("userReciver");

        Log.i("TAG", "FriendRequest userToken: "+ userSender.getUserToken());
        if( notificationType.equals("friendRequest")){
            if(intent.hasExtra(getString(R.string.NOTIFICATION_ID_KEY_FRIEND_REQUEST))){
                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(intent.getIntExtra(getString(R.string.NOTIFICATION_ID_KEY_FRIEND_REQUEST),0));

                FirebaseMessaging.getInstance().subscribeToTopic(userSender.getEmail().replace("@","AT"));
                new PushNotificationSenderService(this,userSender,userReciver).sendFriendRequestResponseNotification();
            }

            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}