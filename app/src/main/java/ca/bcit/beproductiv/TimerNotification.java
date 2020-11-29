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

import ca.bcit.beproductiv.Tabs.TimerFragment;

public class TimerNotification {
    public enum NotificationType {
        SHORT_BREAK(1),
        LONG_BREAK(2),
        INTERVAL(3),
        COMPLETE(4);

        private final int ID;
        NotificationType(int id) {
            this.ID = id;
        }
        public int getId() {
            return this.ID;
        }
    }

    public static void send_notification(Activity context, TimerNotification.NotificationType notificationType) {
        SharedPreferences sharedConfig = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sharedConfig.getBoolean("notifications", false)) { return; }

        int focusSecs = Integer.parseInt(sharedConfig.getString("interval_focus", "1200"));
        int longBreakSecs = Integer.parseInt(sharedConfig.getString("interval_long_break", "900"));
        int shortBreakSecs = Integer.parseInt(sharedConfig.getString("interval_break", "300"));

        int focusMin = (int) Math.ceil(focusSecs / 60.0);
        int longBreakMin = (int) Math.ceil(longBreakSecs / 60.0);
        int shortBreakMin = (int) Math.ceil(shortBreakSecs / 60.0);
        String autoStartInterval = sharedConfig.getString("auto_start_interval", "start_manually");

        String text;
        boolean isManual = autoStartInterval.equals("start_manually");

        switch(notificationType) {
            case SHORT_BREAK:
                text = isManual ? "It's time to start your " + shortBreakMin + " min break!" : "Your " + shortBreakMin + " min break is starting!";
                init_notification(context, "Short Break", text, notificationType);
                break;
            case LONG_BREAK:
                text = isManual ? "It's time to start your " + longBreakMin + " min break!" : "Your " + longBreakMin + " min break is starting!";
                init_notification(context, "Long Break", text, notificationType);
                break;
            case INTERVAL:
                text = isManual ? "It's time to start being productive for " + focusMin + " min!" : "Your " + focusMin + " min productivity period is starting!";
                init_notification(context, "Be Productive", text, notificationType);
                break;
            case COMPLETE:
                init_notification(context, "Finished", "Time's up, good work!", notificationType);
                break;
            default:
                break;
        }
    }

    private static void init_notification(Activity context, String title, String text, NotificationType notificationType) {
        int ID = notificationType.getId();
        Intent intent = context.getIntent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, String.valueOf(ID))
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentTitle(title)
                .setContentText(text)
                .setVibrate(new long[] {0, 1000})
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        createNotificationChannel(context, title, ID);

        // Clear existing timer notifications
        notificationManager.cancel(NotificationType.SHORT_BREAK.getId());
        notificationManager.cancel(NotificationType.LONG_BREAK.getId());
        notificationManager.cancel(NotificationType.INTERVAL.getId());
        notificationManager.cancel(NotificationType.COMPLETE.getId());

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
