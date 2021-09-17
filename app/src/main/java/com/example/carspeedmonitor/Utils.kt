package com.example.carspeedmonitor

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.vmadalin.easypermissions.EasyPermissions
import java.math.BigDecimal


private const val NOTIFICATION_CHANNEL_ID =  "CarSpeedChannel"
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34



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

 fun requestPermissions(context: Activity) {
     if (hasLocationPermissions(context)) {
         return
     } else {
         EasyPermissions.requestPermissions(
             context,
             "You need to accept location permissions to use this app.",
             REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE,
             Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
         )
     }
 }
    var iconNotification: Bitmap? = null
    var notification: Notification? = null
    var mNotificationManager: NotificationManager? = null


    fun generateForegroundNotification(context: Context,resources:Resources):Notification? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val intentMainLanding = Intent(context, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intentMainLanding, 0)
            iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            if (mNotificationManager == null) {
                mNotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(mNotificationManager != null)
                val notificationChannel =
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "Service Notifications",
                        NotificationManager.IMPORTANCE_MIN)
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }
            val builder = NotificationCompat.Builder(context, "service_channel")

            builder.setContentTitle(StringBuilder(resources.getString(R.string.app_name)).append(" service is running").toString())
                .setTicker(StringBuilder(resources.getString(R.string.app_name)).append("service is running").toString())
                .setContentText("Touch to open") //                    , swipe down for more options.
                .setSmallIcon(R.drawable.ic_baseline_speed)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(0)
                .setOnlyAlertOnce(false)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
            if (iconNotification != null) {
                builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
            }
            builder.color = resources.getColor(R.color.purple_200)

           notification = builder.build()

        }

        return notification
    }







