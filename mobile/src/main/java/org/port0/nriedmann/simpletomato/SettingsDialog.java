package org.port0.nriedmann.simpletomato;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by nicol on 3/3/2016.
 */
public class SettingsDialog extends DialogFragment {

    public interface SettingsDialogListener{
        public void onDialogPositiveClick(int workTime, int breakTime, int longBreakTime, int interval);
    }

    SettingsDialogListener interfaceListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            interfaceListener = (SettingsDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view =inflater.inflate(R.layout.dialog_settings, null);
        final NumberPicker work = ((NumberPicker) view.findViewById(R.id.dialog_settings_time_picker_work));
        final NumberPicker brk = ((NumberPicker) view.findViewById(R.id.dialog_settings_time_picker_break));
        final NumberPicker longBrk = ((NumberPicker) view.findViewById(R.id.dialog_settings_time_picker_long_break));
        final NumberPicker intervals = ((NumberPicker) view.findViewById(R.id.dialog_settings_counter_picker));

        work.setMinValue(1);
        work.setMaxValue(360);
        brk.setMinValue(1);
        brk.setMaxValue(360);
        longBrk.setMinValue(1);
        longBrk.setMaxValue(360);
        intervals.setMinValue(1);
        intervals.setMaxValue(100);

        //TODO: Load currently set values
        work.setValue(getArguments().getInt(getString(R.string.work_time)));
        brk.setValue(getArguments().getInt(getString(R.string.break_time)));
        longBrk.setValue(getArguments().getInt(getString(R.string.long_break_time)));
        intervals.setValue(getArguments().getInt(getString(R.string.long_break_interval)));

        builder.setTitle(R.string.action_settings)
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        interfaceListener.onDialogPositiveClick(work.getValue(),brk.getValue(),longBrk.getValue(),intervals.getValue());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        return;
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
