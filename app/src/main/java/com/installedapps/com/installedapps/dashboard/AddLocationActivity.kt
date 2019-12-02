package com.installedapps.com.installedapps.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
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

import kotlinx.android.synthetic.main.activity_add_location.*

import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class AddLocationActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, SeekBar.OnSeekBarChangeListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)
        setSupportActionBar(toolbar)

        mSeekBar = findViewById(R.id.seekBar)
        mSeekBar.max = MAX_RADIUS - MIN_RADIUS
        mSeekBar.setOnSeekBarChangeListener(this)

        val mSaveButton: Button = findViewById(R.id.saveButton)
        mSaveButton.setOnClickListener {
            // todo: save
            finish()
        }

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
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = false
        // todo: for edits, take stored values instead of current location
        mFusedLocationProviderClient.lastLocation.addOnCompleteListener(this) {
            val location = it.result
            if (location != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude), DEFAULT_ZOOM
                ))
            }
        }
        mMap.setOnMapClickListener(this)
        mMap.setOnMarkerDragListener(this)
    }

    override fun onMapClick(loc: LatLng) {
        if (mMarker != null) {
            mMarker!!.position = loc
            mCircle!!.center = loc
        } else {
            mMarker = mMap.addMarker(MarkerOptions().position(loc).draggable(true))
            mCircle = mMap.addCircle(CircleOptions().center(loc).radius((MIN_RADIUS + seekBar!!.progress).toDouble())
                    .strokeWidth(10.0f)
                    .strokeColor(Color.GREEN)
                    .fillColor(Color.argb(128, 128, 255, 128)))
        }
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
