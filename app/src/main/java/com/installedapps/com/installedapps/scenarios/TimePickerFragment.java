package com.installedapps.com.installedapps.scenarios;

import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import java.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private boolean isStart;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = getArguments().getInt("hour");
        int minute = getArguments().getInt("minute");
        isStart = getArguments().getBoolean("isStart");

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if(isStart) {
            ((AddTimeActivity) getActivity()).setStartTime(hourOfDay, minute);
        } else {
            ((AddTimeActivity) getActivity()).setEndTime(hourOfDay, minute);
        }
    }
}
