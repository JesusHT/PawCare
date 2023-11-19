package com.app.pawcare

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
            finish()
        }
    }

}