package ca.bcit.beproductiv;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

public class TimerNotification {
    private enum NotificationID {
        SHORT_BREAK, LONG_BREAK, INTERVAL_START, COMPLETE
    }

    public static void notifyShortBreak(Activity context) {
        init_notification(context, "Short Break", "It's time for a break!", NotificationID.SHORT_BREAK.ordinal());
    }

    public static void notifyLongBreak(Activity context) {
        init_notification(context, "Long Break", "It's time for a break!", NotificationID.LONG_BREAK.ordinal());
    }

    public static void notifyIntervalStart(Activity context) {
        init_notification(context, "Work Time", "Let's get back to work!", NotificationID.INTERVAL_START.ordinal());
    }

    public static void notifyComplete(Activity context) {
        init_notification(context, "Todo Complete", "Time is up!", NotificationID.COMPLETE.ordinal());
    }

    private static void init_notification(Activity context, String title, String text, int ID) {
        SharedPreferences sharedConfig = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sharedConfig.getBoolean("notifications", false)) { return; }

        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, String.valueOf(ID))
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentTitle(title)
                .setContentText(text)
                .setVibrate(new long[] {0, 1000})
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        createNotificationChannel(context, title, ID);
        notificationManager.notify(ID, builder.build());
    }

    private static void createNotificationChannel(Activity context, String name, int ID) {
        // Required for API 26 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(ID),
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
