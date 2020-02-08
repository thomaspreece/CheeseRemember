package com.thomaspreece.nameremember;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationAlarmService extends Service{

        // Unique Identification Number for the Notification.
        // We use it on Notification start, and to cancel it.
        private int NOTIFICATION = 1;

        private NotificationManager mNM;

        @Override
        public IBinder onBind(Intent arg0)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onCreate() {
            mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        }


        @Override
        public int onStartCommand(Intent intent, int flags ,int startId)
        {
            showNotification();
            /*
            var builder = new NotificationCompat.Builder (context)
                    .SetContentIntent (contentIntent)
                    .SetSmallIcon (Resource.Drawable.ic_launcher)
                    .SetContentTitle(title)
                    .SetContentText(message)
                    .SetStyle(style)
                    .SetWhen(Java.Lang.JavaSystem.CurrentTimeMillis())
                    .SetAutoCancel(true)
                    .Extend(wearableExtender);

            var notification = builder.Build();
            manager.Notify(0, notification);

            */
            return START_NOT_STICKY;
        }

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.Notification);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.NotificationTag))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

}
