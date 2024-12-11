package com.example.smartliving.Handlers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smartliving.R;

public class NotificationHandler {
    public void showNotificationWithIntent(Context context, String title, String content, Class activity) {

        if (content == null){
            return;
        }

        // Create an explicit intent for launching Request.class when the notification is clicked
        Intent intent = new Intent(context, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_removebg_preview, options);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.image_removebg_preview)
                .setLargeIcon(largeIconBitmap)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker("Notification")
                .setSound(soundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{100, 340, 200, 340})
                .setContentIntent(pendingIntent) // Set the pending intent
                .setAutoCancel(true); // Dismiss notification on click

        // Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(/*notification_id*/ 0, builder.build());
    }

    public void showNotification(Context context, String title, String content){

    }
}
