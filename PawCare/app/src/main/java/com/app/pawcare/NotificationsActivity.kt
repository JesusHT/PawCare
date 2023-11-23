package com.app.pawcare

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {
    private lateinit var b : ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        b = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.back.setOnClickListener {
            val notificationId = intent.getIntExtra("notificationId", 0)
            if (notificationId != 0 ){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                finish()
            }
        }
        cancelNotification()
    }

    private fun cancelNotification() {
        val notificationId = intent.getIntExtra("notificationId", 0)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

}