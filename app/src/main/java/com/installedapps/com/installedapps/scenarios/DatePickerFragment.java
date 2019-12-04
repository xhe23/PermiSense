package com.installedapps.com.installedapps.scenarios;

import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private boolean isStart;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = getArguments().getInt("year");
        if(year > 2100) {
            year -= 100;
        }
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        isStart = getArguments().getBoolean("isStart");

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if(isStart) {
            ((EditScheduleActivity) getActivity()).setStartDate(month, day, year);
        } else {
            ((EditScheduleActivity) getActivity()).setEndDate(month, day, year);
        }
    }
}
