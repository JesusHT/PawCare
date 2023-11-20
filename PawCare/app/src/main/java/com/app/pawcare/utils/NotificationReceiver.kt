package com.app.pawcare.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val message = intent.getStringExtra("message")
            val title = intent.getStringExtra("title")

            val notificationId = intent.getIntExtra("notificationId", 0)

            showNotification(context, message, title, notificationId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ServiceCast")
    private fun showNotification(context: Context, message: String?, title: String?, notificationId: Int) {
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

        val builder = Notification.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)

        notificationManager.notify(notificationId, builder.build())
    }
}

