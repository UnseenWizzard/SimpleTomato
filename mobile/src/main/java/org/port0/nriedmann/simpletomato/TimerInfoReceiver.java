package org.port0.nriedmann.simpletomato;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by nicol on 3/3/2016.
 */
public class TimerInfoReceiver extends BroadcastReceiver {

//    public interface TimerInfoListener{
//        public void onTimerUpdate(/*need anything here at all?*/);
//    }
//
//    TimerInfoListener interfaceListener;
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            interfaceListener = (SettingsDialogListener) activity;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(activity.toString()
//                    + " must implement NoticeDialogListener");
//        }
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SIMPLETOMATO", "timer received");
        boolean timer_done = intent.getBooleanExtra(TimerService.IS_DONE,false);

        if (timer_done){
            //reset the view
        } else {
            //update the view, a minute has passed
        }


    }

}