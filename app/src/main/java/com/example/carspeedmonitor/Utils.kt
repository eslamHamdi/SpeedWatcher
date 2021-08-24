package com.example.carspeedmonitor

import android.Manifest
import android.content.Context
import com.vmadalin.easypermissions.EasyPermissions
import java.math.BigDecimal


private const val NOTIFICATION_CHANNEL_ID =  "CarSpeedChannel"

fun hasLocationPermissions(context: Context) =

        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )

fun round(unrounded: Double, precision: Int, roundingMode: Int): Double {
    val bd = BigDecimal(unrounded)
    val rounded: BigDecimal = bd.setScale(precision, roundingMode)
    return rounded.toDouble()
}

