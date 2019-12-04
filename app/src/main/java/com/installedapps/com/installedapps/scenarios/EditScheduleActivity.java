package com.installedapps.com.installedapps.scenarios;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.installedapps.com.installedapps.AppDatabase;
import com.installedapps.com.installedapps.R;
import com.installedapps.com.installedapps.dao.ScenarioDao;
import com.installedapps.com.installedapps.model.Scenario;
import com.installedapps.com.installedapps.model.ScenarioDef;
import com.installedapps.com.installedapps.model.ScenarioTimeDef;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class EditScheduleActivity extends AppCompatActivity {
    private static final DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    private static final DateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    private Button mStartTimeButton, mStartDateButton, mEndTimeButton,
            mEndDateButton, mSaveButton, mDeleteButton;
    private ToggleButton mRepeatButton;
    private EditText mName;
    private Calendar startCal, endCal;
    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        mStartTimeButton = (Button)findViewById(R.id.start_time_button);
        mStartDateButton = (Button)findViewById(R.id.start_date_button);
        mEndTimeButton = (Button)findViewById(R.id.end_time_button);
        mEndDateButton = (Button)findViewById(R.id.end_date_button);
        mSaveButton = (Button)findViewById(R.id.save_schedule_button);
        mRepeatButton = (ToggleButton) findViewById(R.id.repeat_button);
        mDeleteButton = (Button)findViewById(R.id.delete_schedule_button);
        mName = (EditText)findViewById(R.id.timeNameEdit);

        long startMil, endMil;
        Intent i = getIntent();
        String name = i.getStringExtra("name");
        if (name != null) {
            isUpdate = true;
            mName.setText(name);
            mName.setEnabled(false);
            ScenarioTimeDef storedDef = (ScenarioTimeDef)ScenarioDef.Converter.stringToScenarioDef(i.getStringExtra("definition"));
            startMil = storedDef.startTime;
            endMil = storedDef.endTime;
        } else {
            startMil = System.currentTimeMillis();
            endMil = System.currentTimeMillis() + 3600000;
            mDeleteButton.setEnabled(false);
        }
        checkSave();

        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        startCal.setTimeInMillis(startMil);
        endCal.setTimeInMillis(endMil);

        mStartTimeButton.setText(timeFormat.format(startCal.getTime()));
        mStartDateButton.setText(dateFormat.format(startCal.getTime()));
        mEndTimeButton.setText(timeFormat.format(endCal.getTime()));
        mEndDateButton.setText(dateFormat.format(endCal.getTime()));
        mRepeatButton.setChecked(startCal.get(Calendar.YEAR) > 2100);
        mRepeatButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startCal.add(Calendar.YEAR, 100);
                } else if(startCal.get(Calendar.YEAR) > 2100) {
                    startCal.add(Calendar.YEAR, -100);
                }
            }
        });
        mName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkSave();
            }
        });
    }

    public void saveValue(View v) {
        if(!checkSave()) {
            return;
        }
        mSaveButton.setEnabled(false);
        Scenario s = new Scenario();
        s.name = mName.getText().toString();
        ScenarioTimeDef def = new ScenarioTimeDef();
        def.startTime = startCal.getTimeInMillis();
        def.endTime = endCal.getTimeInMillis();
        s.definition = def;
        AsyncTask addRuleTask=new AsyncTask<Object,Void,Integer>() {

            @Override
            protected Integer doInBackground(Object... params) {
                ScenarioDao dao = AppDatabase.getInstance(EditScheduleActivity.this).scenarioDao();
                if (isUpdate) {
                    Log.i("PermiSense", "update");
                    dao.update(s);
                } else {
                    Log.i("PermiSense", "insert");
                    dao.insert(s);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer result) {
                finish();
            }
        }.execute();
    }

    public void deleteValue(View v) {
        Scenario s = new Scenario();
        s.name = mName.getText().toString();
        AsyncTask addRuleTask=new AsyncTask<Object,Void,Integer>() {

            @Override
            protected Integer doInBackground(Object... params) {
                ScenarioDao dao = AppDatabase.getInstance(EditScheduleActivity.this).scenarioDao();
                Log.i("PermiSense", "insert");
                dao.delete(s);
                return null;
            }

            @Override
            protected void onPostExecute(Integer result) {
                finish();
            }
        }.execute();
    }

    private boolean checkSave() {
        boolean res = !(mName.getText().toString().matches(""));
        mSaveButton.setEnabled(res);
        return res;
    }

    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt("hour", startCal.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", startCal.get(Calendar.MINUTE));
        args.putBoolean("isStart", true);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt("hour", endCal.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", endCal.get(Calendar.MINUTE));
        args.putBoolean("isStart", false);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showStartDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("month", startCal.get(Calendar.MONTH));
        args.putInt("day", startCal.get(Calendar.DAY_OF_MONTH));
        int year = startCal.get(Calendar.YEAR) > 2100 ? startCal.get(Calendar.YEAR) - 100 : startCal.get(Calendar.YEAR);
        args.putInt("year", year);
        args.putBoolean("isStart", true);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showEndDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("month", endCal.get(Calendar.MONTH));
        args.putInt("day", endCal.get(Calendar.DAY_OF_MONTH));
        args.putInt("year", endCal.get(Calendar.YEAR));
        args.putBoolean("isStart", false);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setStartTime(int hour, int minute) {
        startCal.set(Calendar.HOUR_OF_DAY, hour);
        startCal.set(Calendar.MINUTE, minute);
        Date st = startCal.getTime();
        mStartTimeButton.setText(timeFormat.format(st));
    }

    public void setEndTime(int hour, int minute) {
        endCal.set(Calendar.HOUR_OF_DAY, hour);
        endCal.set(Calendar.MINUTE, minute);
        Date et = endCal.getTime();
        mEndTimeButton.setText(timeFormat.format(et));
    }

    public void setStartDate(int month, int day, int year) {
        startCal.set(Calendar.MONTH, month);
        startCal.set(Calendar.DAY_OF_MONTH, day);
        startCal.set(Calendar.YEAR, year);
        mRepeatButton.setChecked(false);
        Date st = startCal.getTime();
        mStartDateButton.setText(dateFormat.format(st));
    }

    public void setEndDate(int month, int day, int year) {
        endCal.set(Calendar.MONTH, month);
        endCal.set(Calendar.DAY_OF_MONTH, day);
        endCal.set(Calendar.YEAR, year);
        mRepeatButton.setChecked(false);
        Date et = endCal.getTime();
        mEndDateButton.setText(dateFormat.format(et));
    }
}
