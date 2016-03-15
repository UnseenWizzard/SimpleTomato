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
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;


public class MainActivity extends AppCompatActivity implements SettingsDialog.SettingsDialogListener, Observer {

    private int time;

    private int work_time;
    private int break_time;
    private int long_break;
    private int long_break_interval;

    private int work_counter;

    private boolean timer_running;
    private boolean break_now;

    private long timer_start_time;
    private long timer_end_time;

    private TextView timer_text;
    private CircleView circle;
    private CounterView counter_view;

    private CountDownTimer current_timer;
    private AlarmManager current_alarm;
    private PendingIntent alarm_intent;

    private final String TIMER_RUNNING = "TIMER_RUNNING";
    private final String TIMER_START = "TIMER_START";
    private final String TIMER_END = "TIMER_END";
    public static final long ONE_MINUTE = 59950;

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
            startTimer(-1);
        }
    };

    private void updateCounterView(){
        if (work_counter != counter_view.getCount()) {
            counter_view.setCount(work_counter);
            counter_view.forceLayout();
        }
    }

    private void setNextTime(){
        if (break_now){
            time = break_time;
            if (work_counter == long_break_interval){
                time = long_break;
            }
        } else {
            time = work_time;
            if (work_counter >= long_break_interval){
                work_counter =0;
                updateCounterView();
            }
        }
    }

    public void startTimer(long time_to_go){
        long anim_time = time_to_go;
        if (!timer_running){
            //timer not running, start everything
            String msg = (break_now)?"Time for a break!":"Get to work!";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            timer_running =true;
            //start the timer service
            Intent timer_intent = new Intent(this,TimerService.class);
            timer_intent.putExtra(TimerService.RUN_TIME_MS, getMinutesInMillis(time));
            startService(timer_intent);
            //save start and end time
            timer_start_time = Calendar.getInstance().getTimeInMillis();
            timer_end_time = timer_start_time+getMinutesInMillis(time);
            anim_time = ONE_MINUTE;
        }
        //set animation
        CircleViewAnimation anim = new CircleViewAnimation(circle,360);
        anim.setDuration(anim_time);
        circle.startAnimation(anim);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("Resume CALLED", "whats wrong");
        this.loadPreferences();
        this.savePreferences(timer_running,true);
//        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.pref_file),MODE_PRIVATE);
//        long saved_ms = prefs.getLong(getString(R.string.saved_ms),-1);
//        if (timer_running && saved_ms > -1) {
//            long timeleft = 59950 - saved_ms;
//            float angle = 360/59950*timeleft;
//            circle.setAngle(angle);
//            CircleViewAnimation anim = new CircleViewAnimation(circle, 360);
//            anim.setDuration(timeleft);
//            circle.startAnimation(anim);
//        }

        //timer_text.setText("" + time);
        //counter_view.setCount(this.work_counter);
        //counter_view.setMax_count(this.long_break_interval);
        //counter_view.forceLayout();
    }
//TODO:  MAKE THE TIMER A (intent) SERVICE, should save you from running problems, and can replace the additional alarm for the Notification (just send when the timer is done)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Create CALLED", "redoing layout");
        this.loadPreferences();
        this.savePreferences(timer_running, true);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.timer_text = ((TextView) findViewById(R.id.timer_text));
        this.circle = (CircleView) findViewById(R.id.timer_circle);
        this.counter_view = (CounterView) findViewById(R.id.counter_view);
        counter_view.setCount(this.work_counter);
        counter_view.setMax_count(this.long_break_interval);
        if (savedInstanceState == null) {
            setNextTime();
            timer_text.setText("" + time);
            circle.setOnClickListener(viewClick);
            counter_view.forceLayout();
        } else {
            Log.i("CREATE", "TIMER STILL RUNNING, HANDLE THIS CREATE DIFFERENTLY");
            //TODO: Set timer display to correct time
            long current_time = Calendar.getInstance().getTimeInMillis();
            if (savedInstanceState.getLong(TIMER_END) > current_time){
                //timer end not passed yet
                long time_left = savedInstanceState.getLong(TIMER_END)-current_time;
                long time_passed = current_time - savedInstanceState.getLong(TIMER_START);
                //set circle to correct display position
                double current_minute_progress = (double)time_left/ONE_MINUTE;
                long ms_left_of_minute = Math.round(ONE_MINUTE * (((current_minute_progress * 100) % 100) / 100));
                circle.setAngle(360/ONE_MINUTE*(ONE_MINUTE-ms_left_of_minute));
                startTimer(ms_left_of_minute);
            } else {
                //timer end was passed
                //set new time
                setNextTime();
                // display
                timer_text.setText(""+time);
                counter_view.setCount(work_counter);
                counter_view.forceLayout();
                circle.setAngle(0);
                circle.forceLayout();
            }

        }
        TimerObservable.getInstance().addObserver(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("start CALLED", "nothing");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("pause CALLED", "saving");
        this.savePreferences(false,false);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i("stop CALLED", "saving");
        this.savePreferences(false,false);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("destroy CALLED", "bye");
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
        //TODO HANDLE KILLING OF SERVICE TIMER
        Log.i("FINISH CALLED", "TIMERS OFF");
        if (current_timer != null) {
            this.current_timer.cancel();
        }
        if (current_alarm != null) {
            this.current_alarm.cancel(alarm_intent);
        }
        this.timer_running =false;
        this.savePreferences(false,false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TIMER_RUNNING, timer_running);
        outState.putLong(TIMER_START, timer_start_time);
        outState.putLong(TIMER_END, timer_end_time);
        super.onSaveInstanceState(outState);
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
            b.putInt(getString(R.string.work_time), work_time);
            b.putInt(getString(R.string.break_time), break_time);
            b.putInt(getString(R.string.long_break_time), long_break);
            b.putInt(getString(R.string.long_break_interval), long_break_interval);
            DialogFragment settingsDialog = new SettingsDialog();
            settingsDialog.setArguments(b);
            settingsDialog.show(getFragmentManager(), "settings");
        } else if (id == R.id.action_about){
//            SpannableString message = new SpannableString(getString(R.string.about_text));
//            Linkify.addLinks(message,Linkify.ALL);
            TextView messageView = new TextView(this);
            messageView.setText(Html.fromHtml(getString(R.string.about_text)));
            messageView.setMovementMethod(LinkMovementMethod.getInstance());
            messageView.setClickable(true);
            new AlertDialog.Builder(this)
                    .setView(messageView)
                    .setCancelable(true)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDialogPositiveClick(int workTime, int breakTime, int longBreakTime, int interval){
        this.work_time = workTime;
        this.break_time = breakTime;
        this.long_break = longBreakTime;
        this.long_break_interval = interval;
        this.savePreferences(false,true);
        if (!timer_running) {
            if (break_now){
                time = breakTime;
                if (long_break_interval == work_counter){
                    time= longBreakTime;
                }
            } else {
                time = workTime;
            }
            ((TextView)findViewById(R.id.timer_text)).setText("" + time);
        }
        counter_view.setMax_count(this.long_break_interval);
        counter_view.forceLayout();
    }

    private void savePreferences(boolean justRunningInfo, boolean activityRunning){
        SharedPreferences pref = this.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(getString(R.string.timer_running), this.timer_running);
        editor.putBoolean(getString(R.string.activity_running),activityRunning);
        if (!justRunningInfo) {
            editor.putInt(getString(R.string.work_time), this.work_time);
            editor.putInt(getString(R.string.break_time), this.break_time);
            editor.putInt(getString(R.string.long_break_time), this.long_break);
            editor.putInt(getString(R.string.long_break_interval), this.long_break_interval);
            editor.putInt(getString(R.string.work_counter), this.work_counter);
            editor.putBoolean(getString(R.string.break_now), this.break_now);
        }
        editor.commit();
    }

    private void loadPreferences(){
        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        work_time = prefs.getInt(getString(R.string.work_time),25);
        break_time = prefs.getInt(getString(R.string.break_time),5);
        long_break = prefs.getInt(getString(R.string.long_break_time),30);
        long_break_interval = prefs.getInt(getString(R.string.long_break_interval),4);
        work_counter = prefs.getInt(getString(R.string.work_counter),0);
        timer_running = prefs.getBoolean(getString(R.string.timer_running), false);
        break_now = prefs.getBoolean(getString(R.string.break_now),false);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof TimerObservable.TimerInfo) {
            final TimerObservable.TimerInfo info = (TimerObservable.TimerInfo) data;
            loadPreferences();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("ST_TIMER", "observed an update!");
                    if (info.is_done()){
                        //TODO: timer done, setNext time, reset the view,
                        //reset circle
                        CircleViewAnimation resetAnim = new CircleViewAnimation(circle,0);
                        resetAnim.setDuration(700);
                        circle.startAnimation(resetAnim);
                        //set new time
                        setNextTime();
                        // display
                        timer_text.setText(""+time);
                        counter_view.setCount(work_counter);
                        counter_view.forceLayout();
                    } else {
                        //TODO: timer still running, a minute has passed, update the circle & the timer text
                        timer_text.setText(""+getMillisInMin(info.getTime_till_done_ms()));
                        circle.setAngle(0.0f);
                        //set animation
                        CircleViewAnimation anim = new CircleViewAnimation(circle,360);
                        anim.setDuration(ONE_MINUTE);
                        circle.startAnimation(anim);
                    }
                }
            });
        }
    }
}
