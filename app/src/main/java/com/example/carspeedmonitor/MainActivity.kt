package com.example.carspeedmonitor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.media.session.PlaybackState.ACTION_STOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.carspeedmonitor.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.math.BigDecimal

class MainActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks {


    lateinit var speedService: SpeedService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView()

        val binding:ActivityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main)
        speedService = SpeedService()
        requestPermissions(this)


        lifecycleScope.launch {

            speedService._speedFlow.collect {
                binding.speedIndicator.speedTo(it.toFloat())

            }
        }
    }







    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
        {
            SettingsDialog.Builder(this).build().show()
        } else
        {
            requestPermissions(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

        startService(Intent(this,SpeedService::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onDestroy() {
        super.onDestroy()
        val intentStop = Intent(this, SpeedService::class.java)
        intentStop.action = ACTION_STOP.toString()
        startService(intentStop)
    }

}



