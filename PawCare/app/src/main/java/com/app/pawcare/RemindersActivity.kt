package com.app.pawcare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityRemindersBinding

class RemindersActivity : AppCompatActivity() {
    lateinit var b : ActivityRemindersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRemindersBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.back.setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}