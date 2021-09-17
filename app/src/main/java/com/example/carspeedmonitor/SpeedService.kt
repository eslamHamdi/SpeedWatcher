package com.example.carspeedmonitor

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.BitmapFactory
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

class SpeedService:Service() {

    private  var fusedLocationClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? =null
    val mNotificationId = 123

    private var speedFlow: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val _speedFlow = speedFlow.asStateFlow()

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        getUserLocation(this)


        return START_STICKY
    }


    private fun stopLocationUpdates()
    {
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(it)
        }

    }

    @SuppressLint("MissingPermission", "VisibleForTests")
    fun getUserLocation(context: Context)
    {


        fusedLocationClient = FusedLocationProviderClient(context)
        if (hasLocationPermissions(context)) {


            startForeground(mNotificationId, generateForegroundNotification(context,resources))
            val locationRequest = LocationRequest.create().apply {
                interval = 1000
                fastestInterval = 1000
                smallestDisplacement = 0f
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                Log.d(null, "getuserlocation: entered1")
            }

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(locationRequest)
            val client: SettingsClient = LocationServices.getSettingsClient(context)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {

                val flag = it.locationSettingsStates?.isLocationUsable
                if (flag == true) {
                    locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            var locationList = locationResult.locations

                            if (locationList.size > 0) {
                                val location: Location = locationList[locationList.size - 1]
                                val location2: Location = locationResult.lastLocation
                                val lat = location.latitude
                                val lng = location.longitude

                                val speed = location.speed.toDouble()
                                val currentSpeed = round(speed, 3, BigDecimal.ROUND_HALF_UP)
                                val kmphSpeed = round(currentSpeed * 3.6, 3, BigDecimal.ROUND_HALF_UP)
                                speedFlow.value = kmphSpeed
                            }

                        }
                    }
                    fusedLocationClient!!.requestLocationUpdates(
                        locationRequest,
                        locationCallback!!,
                        Looper.getMainLooper()
                    )
                }


            }
            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    Log.d(null, "setuserlocation: location failure")
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        // exception.startResolutionForResult(context., 0x1)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }

//    } else
//    {
//        requestPermissions(context)
//
//    }


    }


    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }




}