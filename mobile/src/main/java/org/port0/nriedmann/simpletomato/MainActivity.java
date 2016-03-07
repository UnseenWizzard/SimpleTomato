package org.port0.nriedmann.simpletomato;

import android.app.AlarmManager;

import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SettingsDialog.SettingsDialogListener {

    private int time;

    private int workTime;
    private int breakTime;
    private int longBreak;
    private int longBreakInterval;

    private int workCounter;

    private boolean timerRunning;
    private boolean breakNow;

    private TextView timerText;
    private CircleView circle;
    private CounterView counterView;

    private CountDownTimer currentTimer;
    private AlarmManager currentAlarm;
    private PendingIntent alarmIntent;

    private long getMinutesInMillis(int minutes){
        return minutes*60*1000;
    }
    private long getMillisInMin(long millis){
        double mil = (double)millis;
        return (long) Math.round(mil / 60000.0);
    }

    private View.OnClickListener viewClick = new View.OnClickListener() {
        public void onClick(View v) {
            Log.i("CLICK", "clicked the view");
            startTimer();
        }
    };

    private void setNextTime(){
        if (breakNow){
            time = breakTime;
            if (workCounter== longBreakInterval){
                workCounter=0;
                counterView.update(workCounter);
                time = longBreak;
            }
        } else {
            time = workTime;
        }
    }

    private void finishTimer(){
        Log.i("Timer DONE: ", "done");
        //advance work counter
        workCounter = (breakNow)?workCounter:workCounter+1;
        //toggle break bool
        breakNow = !breakNow;
        //display counter
        if (workCounter!=counterView.getCount()){
            counterView.update(workCounter);
        }
        //reset circle
        CircleViewAnimation resetAnim = new CircleViewAnimation(circle,0);
        resetAnim.setDuration(700);
        circle.startAnimation(resetAnim);
        //set new time
        setNextTime();
        // display
        ((TextView)findViewById(R.id.timer_text)).setText(""+time);
        // reset timerrunning
        timerRunning=false;
        savePreferences(true);
    }

    public void startTimer(){
        if (!timerRunning){
            String msg = (breakNow)?"Time for a break!":"Get to work!";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            timerRunning=true;
            //set animation
            //CircleView circle = (CircleView) findViewById(R.id.timer_circle);
            CircleViewAnimation anim = new CircleViewAnimation(circle,360);
            anim.setDuration(getMinutesInMillis(time));

            //set notification timer
            currentAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, TimerNotificationSender.class);
            intent.putExtra("org.port0.nriemdann.SimpleTomato.isBreak", breakNow);
            alarmIntent = PendingIntent.getBroadcast(this, 42, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MILLISECOND, (int) getMinutesInMillis(time));
            long notificationTime = c.getTimeInMillis();
            currentAlarm.set(AlarmManager.RTC_WAKEUP, notificationTime, alarmIntent);

            //set countdown timer
            currentTimer = new CountDownTimer(getMinutesInMillis(time),59500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerText.setText("" + (getMillisInMin(millisUntilFinished)));
                    SharedPreferences pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putLong(getString(R.string.saved_ms), millisUntilFinished);
                    editor.commit();
                    Log.i("Timer TICK: ", "ms " + millisUntilFinished);
                    Log.i("Timer TICK: ", "min " + getMillisInMin(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    finishTimer();
                }
            }.start();
            circle.startAnimation(anim);
            Log.i("Starting Timer: ", "Time " + time);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        this.loadPreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.savePreferences(false);
    }

    @Override
    public void onStop(){
        super.onStop();
        this.savePreferences(false);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.confirm_exit))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton(getString(R.string.stay), null)
                .show();
    }

    @Override
    public void finish(){
        super.finish();
        if (currentTimer != null) {
            this.currentTimer.cancel();
        }
        if (currentAlarm != null) {
            this.currentAlarm.cancel(alarmIntent);
        }
        this.timerRunning=false;
        this.savePreferences(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.loadPreferences();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNextTime();
        this.timerText = ((TextView)findViewById(R.id.timer_text));
        timerText.setText("" + time);
        this.circle = (CircleView)findViewById(R.id.timer_circle);
        circle.setOnClickListener(viewClick);
        this.counterView = (CounterView)findViewById(R.id.counter_view);
        counterView.setCount(this.workCounter);
        counterView.setMax_count(this.longBreakInterval);
        counterView.forceLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
          //  Toast.makeText(MainActivity.this, "SETTINGS", Toast.LENGTH_SHORT).show();
            Bundle b = new Bundle();
            b.putInt(getString(R.string.work_time),workTime);
            b.putInt(getString(R.string.break_time),breakTime);
            b.putInt(getString(R.string.long_break_time),longBreak);
            b.putInt(getString(R.string.long_break_interval),longBreakInterval);
            DialogFragment settingsDialog = new SettingsDialog();
            settingsDialog.setArguments(b);
            settingsDialog.show(getFragmentManager(), "settings");
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDialogPositiveClick(int workTime, int breakTime, int longBreakTime, int interval){
        this.workTime = workTime;
        this.breakTime = breakTime;
        this.longBreak = longBreakTime;
        this.longBreakInterval = interval;
        this.savePreferences(false);
        if (!timerRunning) {
            if (breakNow){
                time = breakTime;
                if (longBreakInterval==workCounter){
                    time= longBreakTime;
                }
            } else {
                time = workTime;
            }
            ((TextView)findViewById(R.id.timer_text)).setText("" + time);
        }
    }

    private void savePreferences(boolean justRunningInfo){
        SharedPreferences pref = this.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(getString(R.string.timer_running), this.timerRunning);
        if (!justRunningInfo) {
            editor.putInt(getString(R.string.work_time), this.workTime);
            editor.putInt(getString(R.string.break_time), this.breakTime);
            editor.putInt(getString(R.string.long_break_time), this.longBreak);
            editor.putInt(getString(R.string.long_break_interval), this.longBreakInterval);
            editor.putInt(getString(R.string.work_counter), this.workCounter);
            editor.putBoolean(getString(R.string.break_now), this.breakNow);
        }
        editor.commit();
    }

    private void loadPreferences(){
        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        workTime = prefs.getInt(getString(R.string.work_time),25);
        breakTime = prefs.getInt(getString(R.string.break_time),5);
        longBreak = prefs.getInt(getString(R.string.long_break_time),30);
        longBreakInterval = prefs.getInt(getString(R.string.long_break_interval),4);
        workCounter = prefs.getInt(getString(R.string.work_counter),0);
        timerRunning = prefs.getBoolean(getString(R.string.timer_running), false);
        breakNow = prefs.getBoolean(getString(R.string.break_now),false);
    }

}
