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
    public enum NotificationType {
        SHORT_BREAK, LONG_BREAK, INTERVAL, COMPLETE
    }

    public static void send_notification(Activity context, TimerNotification.NotificationType notificationType) {
        SharedPreferences sharedConfig = PreferenceManager.getDefaultSharedPreferences(context);
        String focusMin = sharedConfig.getString("interval_focus", "30");
        String longBreakMin = sharedConfig.getString("interval_long_break", "15");
        String shortBreakMin = sharedConfig.getString("interval_break", "5");
        String autoStartInterval = sharedConfig.getString("auto_start_interval", "start_manually");

        String text;
        switch(notificationType) {
            case SHORT_BREAK:
                text = autoStartInterval.equals("start_manually") ? "It's time to start your " + shortBreakMin + " min break!" : "Your " + shortBreakMin + " min break is starting!";
                init_notification(context, "Short Break", text, 1);
                break;
            case LONG_BREAK:
                text = autoStartInterval.equals("start_manually") ? "It's time to start your " + longBreakMin + " min break!" : "Your " + longBreakMin + " min break is starting!";
                init_notification(context, "Long Break", text, 2);
                break;
            case INTERVAL:
                text = autoStartInterval.equals("start_manually") ? "It's time to start being productive for " + focusMin + " min!" : "Your " + focusMin + " min productivity period is starting!";
                init_notification(context, "Be Productive", text, 3);
                break;
            case COMPLETE:
                init_notification(context, "Finished", "Time's up, good work!", 4);
                break;
            default:
                break;
        }
    }

    private static void init_notification(Activity context, String title, String text, int ID) {
        SharedPreferences sharedConfig = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sharedConfig.getBoolean("notifications", false)) { return; }

        Intent intent = context.getIntent();
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
        notificationManager.cancelAll(); // Note: If adding non-timer related notifications in the future, change to cancel(id)
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
