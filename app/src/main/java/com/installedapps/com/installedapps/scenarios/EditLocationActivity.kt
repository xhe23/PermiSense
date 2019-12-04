package com.installedapps.com.installedapps.scenarios

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.installedapps.com.installedapps.R

import kotlinx.android.synthetic.main.activity_edit_location.*

import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.installedapps.com.installedapps.AppDatabase
import com.installedapps.com.installedapps.model.Scenario
import com.installedapps.com.installedapps.model.ScenarioDef
import com.installedapps.com.installedapps.model.ScenarioLocationDef


class EditLocationActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, SeekBar.OnSeekBarChangeListener {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0
        private const val DEFAULT_ZOOM = 16.0f
        private const val MIN_RADIUS = 100
        private const val MAX_RADIUS = 1000
    }

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private var mMarker: Marker? = null
    private var mCircle: Circle? = null
    private lateinit var mSeekBar: SeekBar
    private lateinit var mName: EditText
    private lateinit var mSaveButton: Button
    private var update = false
    private var storedDef: ScenarioLocationDef? = null

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_location)
        setSupportActionBar(toolbar)

        mSeekBar = findViewById(R.id.seekBar)
        mSaveButton = findViewById(R.id.saveButton)
        mName = findViewById(R.id.nameEdit)

        mSeekBar.max = MAX_RADIUS - MIN_RADIUS
        mSeekBar.setOnSeekBarChangeListener(this)

        mName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkSave()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        val name = intent.getStringExtra("name")
        if (name != null) {
            update = true
            mName.setText(name)
            mName.isEnabled = false
            storedDef = ScenarioDef.Converter.stringToScenarioDef(intent.getStringExtra("definition")) as ScenarioLocationDef
            mSeekBar.progress = storedDef!!.radius.toInt() - MIN_RADIUS
        }

        mSaveButton.setOnClickListener {
            if (!checkSave()) {
                return@setOnClickListener
            }
            mSaveButton.isEnabled = false
            val s = Scenario()
            s.name = mName.text.toString()
            val def = ScenarioLocationDef()
            def.lat = mMarker!!.position.latitude
            def.lon = mMarker!!.position.longitude
            def.radius = mSeekBar.progress.toDouble() + MIN_RADIUS
            s.definition = def
            object : AsyncTask<Void, Void, Void?>() {
                override fun doInBackground(vararg params: Void?): Void? {
                    val dao = AppDatabase.getInstance(this@EditLocationActivity).scenarioDao()
                    if (update) {
                        Log.i("PermiSense", "update")
                        dao.update(s)
                    } else {
                        Log.i("PermiSense", "insert")
                        dao.insert(s)
                    }
                    return null
                }
                override fun onPostExecute(result: Void?) {
                    finish()
                }
            }.execute()
        }
        mSaveButton.isEnabled = false

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        } else {
            createMap()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    createMap()
                }
            }
        }
    }

    private fun createMap() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = false
        if (update) {
            val loc = LatLng(storedDef!!.lat, storedDef!!.lon)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, DEFAULT_ZOOM))
            setMarker(loc)
        } else {
            mFusedLocationProviderClient.lastLocation.addOnCompleteListener(this) {
                val location = it.result
                if (location != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude), DEFAULT_ZOOM
                    ))
                }
            }
        }
        mMap.setOnMapClickListener(this)
        mMap.setOnMarkerDragListener(this)
    }

    private fun setMarker(loc: LatLng) {
        if (mMarker != null) {
            mMarker!!.position = loc
            mCircle!!.center = loc
        } else {
            mMarker = mMap.addMarker(MarkerOptions().position(loc).draggable(true))
            mCircle = mMap.addCircle(CircleOptions().center(loc).radius((MIN_RADIUS + seekBar!!.progress).toDouble())
                    .strokeWidth(10.0f)
                    .strokeColor(Color.GREEN)
                    .fillColor(Color.argb(128, 128, 255, 128)))
            checkSave()
        }
    }

    private fun checkSave(): Boolean {
        val res = !(mMarker == null || mName.text.isEmpty())
        mSaveButton.isEnabled = res
        return res
    }

    override fun onMapClick(loc: LatLng) {
        setMarker(loc)
    }

    override fun onMarkerDragStart(m: Marker) {
        if (m != mMarker) {
            return
        }
        mCircle!!.center = m.position
    }

    override fun onMarkerDrag(m: Marker) {
        if (m != mMarker) {
            return
        }
        mCircle!!.center = m.position
    }

    override fun onMarkerDragEnd(m: Marker) {
        if (m != mMarker) {
            return
        }
        mCircle!!.center = m.position
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (mCircle != null) {
            mCircle!!.radius = (MIN_RADIUS + progress).toDouble()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        if (mCircle != null) {
            mCircle!!.radius = (MIN_RADIUS + seekBar!!.progress).toDouble()
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (mCircle != null) {
            mCircle!!.radius = (MIN_RADIUS + seekBar!!.progress).toDouble()
        }
    }
}
