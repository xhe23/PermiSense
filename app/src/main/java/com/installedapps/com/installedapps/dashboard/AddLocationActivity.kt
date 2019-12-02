package com.installedapps.com.installedapps.dashboard

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.installedapps.com.installedapps.R

import kotlinx.android.synthetic.main.activity_add_location.*

class AddLocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)
        setSupportActionBar(toolbar)
    }

}
