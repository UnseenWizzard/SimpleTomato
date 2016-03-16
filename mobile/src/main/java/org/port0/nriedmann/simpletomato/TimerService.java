package org.port0.nriedmann.simpletomato;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Created by nicol on 3/10/2016.
 */
public class TimerService extends Service {

    public final static String RUN_TIME_MS = "RUN_TIME_MS";
    public final static String IS_DONE = "IS_DONE";
    private Thread worker;

//    public TimerService(){
//        super("TimerService");
//    }

    @Override
    public void onDestroy() {
        Log.i("Service","Service killed!!");
        super.onDestroy();

    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        //get timer time from intent
        if (intent == null){
            Log.e("Service","Started with null intent");
            return -1;
        }
        final long run_time_ms = intent.getLongExtra(TimerService.RUN_TIME_MS,-1);
        //setup countdown timer, that updates the view via broadcast
        if (run_time_ms>0) {

            //set countdown timer
            final CountDownTimer timer = new CountDownTimer(run_time_ms, MainActivity.ONE_MINUTE) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i("Timerservice TICK: ", "ms " + millisUntilFinished);
                    //check if the activity is running
                    SharedPreferences pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
                    boolean activity_running = pref.getBoolean(getString(R.string.activity_running), false);
                    if (activity_running) {
                        //if running, send update info
//                        Intent info = new Intent();
//                        info.setAction(getString(R.string.timer_info));
//                        info.putExtra(IS_DONE, false);
//                        info.putExtra(getString(R.string.saved_ms), millisUntilFinished);
//                        sendBroadcast(info);
                        TimerObservable.getInstance().update(false, millisUntilFinished);
                    } else {
                        //else save info to shared prefs
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putLong(getString(R.string.saved_ms), millisUntilFinished);
                        editor.putBoolean(getString(R.string.timer_running),true);
                        editor.commit();
                    }
//                   timer_text.setText("" + (getMillisInMin(millisUntilFinished)));
//                    SharedPreferences pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.putLong(getString(R.string.saved_ms), millisUntilFinished);
//                    editor.commit();
//
//                    circle.setAngle(0.0f);
//                    circle.forceLayout();
//                    CircleViewAnimation anim = new CircleViewAnimation(circle,360);
//                    anim.setDuration(59950);
//                    Log.i("onTick","starting anim");
//                    circle.startAnimation(anim);
                }

                @Override
                public void onFinish() {
                    Log.i("Timerservice DONE: ", "done");
                    //check if the activity is running
                    SharedPreferences pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
                    boolean activity_running = pref.getBoolean(getString(R.string.activity_running), false);
                    int work_counter = pref.getInt(getString(R.string.work_counter), 0);
                    boolean break_now = pref.getBoolean(getString(R.string.break_now), false);

                    //advance work counter
                    work_counter = (break_now) ? work_counter : work_counter + 1;
                    //toggle break bool
                    break_now = !break_now;
                    //save info to shared prefs
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putLong(getString(R.string.saved_ms), 0);
                    editor.putBoolean(getString(R.string.timer_running), false);
                    editor.putBoolean(getString(R.string.next_time_set), false);
                    editor.putInt(getString(R.string.work_counter), work_counter);
                    editor.putBoolean(getString(R.string.break_now), break_now);
                    editor.commit();

                    if (activity_running) {
                        //if running, send intent to update info
//                        Intent info = new Intent();
//                        info.setAction(getString(R.string.timer_info));
//                        info.putExtra(IS_DONE, true);
//                        sendBroadcast(info);
                        TimerObservable.getInstance().update(true,0);
                    }

                    //send notification
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext());
                    notification.setSmallIcon(R.drawable.notification_icon);
                    notification.setCategory(Notification.CATEGORY_ALARM);
                    notification.setColor(Color.RED);
                    notification.setDefaults(Notification.DEFAULT_ALL);
                    notification.setContentTitle(getString(R.string.app_name));
                    notification.setAutoCancel(true);
                    if (!break_now) {
                        notification.setContentText(getString(R.string.notification_break_done_text));
                    } else {
                        notification.setContentText(getString(R.string.notification_work_done_text));
                    }
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.setAction(getString(R.string.start_from_notification));
                    notification.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, mainIntent, 0));
                    ((NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE)).notify(42, notification.build());
                    stopSelf(startId);
                }
            };
            worker = new Thread("TimerThread"){
                @Override
                public void run(){
                    timer.start();
                }
            };
            worker.start();
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Looper.loop();
        }
        return START_FLAG_REDELIVERY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
