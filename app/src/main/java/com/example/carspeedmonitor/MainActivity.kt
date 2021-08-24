package com.example.carspeedmonitor

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import java.math.BigDecimal

class MainActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var locationCallback: LocationCallback? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    private fun stopLocationUpdates()
    {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }

    }

    @SuppressLint("MissingPermission")
    fun getUserLocation()
    {
        if (hasLocationPermissions(this))
        {

            val locationRequest = LocationRequest.create().apply {
                interval = 1000
                fastestInterval = 1000
                smallestDisplacement = 0f
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                Log.d(null, "getuserlocation: entered1")
            }

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(locationRequest)
            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {

                val flag = it.locationSettingsStates?.isLocationUsable
                if (flag == true)
                {
                    locationCallback = object : LocationCallback()
                    {
                        override fun onLocationResult(locationResult: LocationResult)
                        {
                            var locationList = locationResult.locations

                            if (locationList.size > 0)
                            {
                                val location: Location = locationList[locationList.size - 1]
                                val location2: Location = locationResult.lastLocation
                                val lat = location.latitude
                                val lng = location.longitude

                                val speed = location.speed.toDouble()
                                val currentSpeed = round(speed, 3, BigDecimal.ROUND_HALF_UP)
                                val kmphSpeed = round(currentSpeed * 3.6, 3, BigDecimal.ROUND_HALF_UP)
                            }

                        }
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
                }


            }
            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException)
                {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    Log.d(null, "setuserlocation: location failure")
                    try
                    {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(this, 0x1)
                    } catch (sendEx: IntentSender.SendIntentException)
                    {
                        // Ignore the error.
                    }
                }
            }


        } else
        {
           requestPermissions()

        }


    }



    private fun requestPermissions()
    {
        if (hasLocationPermissions(this))
        {
            return
        }
        else
        {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
        {
            SettingsDialog.Builder(this).build().show()
        } else
        {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        return
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


}

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34