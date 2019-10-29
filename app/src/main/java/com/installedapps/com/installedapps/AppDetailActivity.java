package com.installedapps.com.installedapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AppDetailActivity extends AppCompatActivity {

    private String mAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        mAppName = i.getStringExtra("app-name");
        TextView nameTextView = (TextView)findViewById(R.id.appNameTextView);
        nameTextView.setText(mAppName);
    }
}
