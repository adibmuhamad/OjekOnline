package com.online.ojek.ojekonline.Helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.online.ojek.ojekonline.Model.Notification;
import com.online.ojek.ojekonline.R;

/**
 * Created by adib on 29/05/18.
 */

public class NotificationHelper extends ContextWrapper {

    private static final String Ojol_Channel_Id = "com.online.ojek.ojekonline.OJOL";
    private static final String Ojol_Channel_Name = "Ojol";

    public NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel ojolChannel = new NotificationChannel(Ojol_Channel_Id,
                Ojol_Channel_Name,
                NotificationManager.IMPORTANCE_DEFAULT);
        ojolChannel.enableLights(true);
        ojolChannel.enableVibration(true);
        ojolChannel.setLightColor(Color.GRAY);
        ojolChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(ojolChannel);
    }

    public NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public android.app.Notification.Builder getOjolNotification(String title, String content, PendingIntent contentIntent,
                                                                Uri soundUri)
    {
        return new android.app.Notification.Builder(getApplicationContext(), Ojol_Channel_Id)
                .setContentText(content)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_ojol);
    }

}
