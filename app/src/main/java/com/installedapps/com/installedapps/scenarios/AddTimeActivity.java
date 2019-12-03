package com.installedapps.com.installedapps.scenarios;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.installedapps.com.installedapps.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class AddTimeActivity extends AppCompatActivity {
    private static final DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    private static final DateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    private Date startTime, startDate, endTime, endDate;
    private Button mStartTimeButton, mStartDateButton, mEndTimeButton, mEndDateButton;
    private Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        mStartTimeButton = (Button)findViewById(R.id.start_time_button);
        mStartDateButton = (Button)findViewById(R.id.start_date_button);
        mEndTimeButton = (Button)findViewById(R.id.end_time_button);
        mEndDateButton = (Button)findViewById(R.id.end_date_button);

        startTime = startDate = new Date();
        cal = Calendar.getInstance();
        cal.setTime(startTime); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        endTime = cal.getTime(); // returns new date object, one hour in the future
        cal.add(Calendar.YEAR, 100);
        endDate = cal.getTime();

        mStartTimeButton.setText(timeFormat.format(startTime));
        mStartDateButton.setText(dateFormat.format(startDate));
        mEndTimeButton.setText(timeFormat.format(endTime));
        //Intent i = getIntent();

    }

    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        cal.setTime(startTime);
        args.putInt("hour", cal.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", cal.get(Calendar.MINUTE));
        args.putBoolean("isStart", true);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        cal.setTime(endTime);
        args.putInt("hour", cal.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", cal.get(Calendar.MINUTE));
        args.putBoolean("isStart", false);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showStartDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        cal.setTime(startDate);
        args.putInt("month", cal.get(Calendar.MONTH));
        args.putInt("day", cal.get(Calendar.DAY_OF_MONTH));
        args.putInt("year", cal.get(Calendar.YEAR));
        args.putBoolean("isStart", true);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showEndDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        cal.setTime(endDate);
        args.putInt("month", cal.get(Calendar.MONTH));
        args.putInt("day", cal.get(Calendar.DAY_OF_MONTH));
        args.putInt("year", cal.get(Calendar.YEAR));
        args.putBoolean("isStart", false);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setStartTime(int hour, int minute) {
        SimpleDateFormat df = new SimpleDateFormat("H:m");
        try {
            startTime = df.parse(hour + ":" + minute);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mStartTimeButton.setText(timeFormat.format(startTime));
    }

    public void setEndTime(int hour, int minute) {
        SimpleDateFormat df = new SimpleDateFormat("H:m");
        try {
            endTime = df.parse(hour + ":" + minute);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mEndTimeButton.setText(timeFormat.format(endTime));
    }

    public void setStartDate(int month, int day, int year) {
        month++;
        SimpleDateFormat df = new SimpleDateFormat("M/d/yyyy");
        try {
            startDate = df.parse(month + "/" + day + "/" + year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mStartDateButton.setText(dateFormat.format(startDate));
    }

    public void setEndDate(int month, int day, int year) {
        month++;
        SimpleDateFormat df = new SimpleDateFormat("M/d/yyyy");
        try {
            endDate = df.parse(month + "/" + day + "/" + year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mEndDateButton.setText(dateFormat.format(endDate));
    }
}
