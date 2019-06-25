package com.android.tradingdiary.utils;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.android.tradingdiary.R;
import com.android.tradingdiary.mainscreen.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {

    private static final long DEF_DAILY_REMIND_TIME = (11 * DateTimeUtils.HOUR);

    public static final String EXTRA_IS_REPEATED = "IS_REPEATED";

    public static final String EXTRA_EVENT_TITLE = "EVENT_TITLE";

    public static final String EXTRA_EVENT_DATE = "EVENT_DATE";

    public static final String EXTRA_EVENT_ID = "EVENT_ID";

    public static final String EXTRA_NOTIFICATION_TITLE = "NOTIFICATION_TITLE";

    public static final String EXTRA_NOTIFICATION_CONTENT = "NOTIFICATION_CONTENT";

    public static final String EVENT_NOTIFICATION_ID = "EVENT_NOTIFICATION";

    public static void buildRepeatedReminder(Context context, long date, String title, String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(EVENT_NOTIFICATION_ID, context.getString(R.string.event_notification), NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        long showDate;
        if (System.currentTimeMillis() >= DateTimeUtils.getTodayStart() + DEF_DAILY_REMIND_TIME) {
            showDate = DateTimeUtils.getTodayStart() + DateTimeUtils.DAY + DEF_DAILY_REMIND_TIME;
        } else {
            long dailyRemindTime = DEF_DAILY_REMIND_TIME;
            showDate = DateTimeUtils.getTodayStart() + dailyRemindTime;
        }
        String dateUnit = DateTimeUtils.convertTimeUnit(context, date - System.currentTimeMillis());
        createAlarmManager(context, date, title, id, showDate, dateUnit, true);
    }

    private static void createAlarmManager(Context context, long date, String title, String id, long showDate, String dateUnit, boolean isRepeated) {
        ComponentName componentName = new ComponentName(context, "com.android.tradingdiary.ReminderReceiver");
        Intent intent = new Intent();
        intent.setData(Uri.parse(id));
        intent.setComponent(componentName);
        intent.putExtra(EXTRA_IS_REPEATED, isRepeated);
        intent.putExtra(EXTRA_NOTIFICATION_TITLE, "Trade of the day");
        intent.putExtra(EXTRA_NOTIFICATION_CONTENT, "Make a smart deal");

        if (isRepeated) {
            intent.putExtra(EXTRA_EVENT_TITLE, title);
            intent.putExtra(EXTRA_EVENT_DATE, date);
            intent.putExtra(EXTRA_EVENT_ID, id);
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, showDate, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, showDate, pendingIntent);
        }
    }

    public static void showNotification(Context context, String title, String content) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context, MainActivity.EVENT_NOTIFICATION_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(1, notification);
    }
}
