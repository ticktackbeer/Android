package com.example.halloworld.Service;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.example.halloworld.Model.User;
import com.example.halloworld.R;

public class DirectReplyService extends Service {
    public DirectReplyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Infos über den Versender der Trink message, benötigt um  in die PushNachricht zu schicken
        User userSender = (User) intent.getSerializableExtra("userSender");
        User userReciver = (User) intent.getSerializableExtra("userReciver");

        if(intent.hasExtra(getString(R.string.NOTIFICATION_ID_KEY_TRINK_REQUEST))){
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(intent.getIntExtra(getString(R.string.NOTIFICATION_ID_KEY_TRINK_REQUEST),0));
        }
        // eingegebener Text

        Bundle directRemoteInfo = RemoteInput.getResultsFromIntent(intent);
        CharSequence inputtext = directRemoteInfo.getCharSequence(getString(R.string.NOTIFICATION_ID_KEY_TRINK_REPLY));

        new PushNotificationSenderService(this,userSender,userReciver).sendTrinkReplyNotification(inputtext.toString());
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}