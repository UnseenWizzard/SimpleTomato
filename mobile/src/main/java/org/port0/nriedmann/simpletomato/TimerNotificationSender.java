package org.port0.nriedmann.simpletomato;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by nicol on 3/3/2016.
 */
public class TimerNotificationSender extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SIMPLETOMATO", "timer received");
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setSmallIcon(R.drawable.notification_icon);
        notification.setCategory(Notification.CATEGORY_ALARM);
        notification.setColor(Color.RED);
        notification.setDefaults(Notification.DEFAULT_ALL);
        notification.setContentTitle(context.getString(R.string.app_name));
        notification.setAutoCancel(true);
        boolean wasBreak = intent.getBooleanExtra("org.port0.nriemdann.SimpleTomato.isBreak", false);
        if (wasBreak == true) {
            notification.setContentText(context.getString(R.string.notification_break_done_text));
        } else {
            notification.setContentText(context.getString(R.string.notification_work_done_text));
        }
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setType(Intent.ACTION_TIME_TICK);
        notification.setContentIntent(PendingIntent.getActivity(context,0,mainIntent,0));
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.pref_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(context.getString(R.string.break_now),!wasBreak);
        editor.putBoolean(context.getString(R.string.timer_running), false);
        editor.commit();

        ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).notify(42, notification.build());
    }

}