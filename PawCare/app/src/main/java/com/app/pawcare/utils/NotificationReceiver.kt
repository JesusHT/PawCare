package com.app.pawcare.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.app.pawcare.NotificationsActivity
import com.app.pawcare.R

class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val message           = intent.getStringExtra("message")
            val title             = intent.getStringExtra("title")
            val typeNotification  = intent.getStringExtra("typeNotification")

            val notificationId = intent.getIntExtra("notificationId", 0)

            showNotification(context, message, title, notificationId, typeNotification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ServiceCast")
    private fun showNotification(context: Context, message: String?, title: String?, notificationId: Int, typeNotification : String?) {
        if (message.isNullOrBlank() || title.isNullOrBlank()) {
            Log.d("ERROR NOTIFICATIONS", "showNotification: No llegan los parametros ")
            return
        }

        val channelId = "default_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val iconNotification = when (typeNotification) {
            "Cita médica"     -> R.drawable.baseline_blue_hospital_24
            "Desparasitación" -> R.drawable.baseline_blue_virus_24
            "Vacunas"         -> R.drawable.baseline_blue_syringe_24
            else              -> throw IllegalArgumentException("Not found")
        }

        val intent = Intent(context, NotificationsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = Notification.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(iconNotification)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, builder.build())
    }
}

