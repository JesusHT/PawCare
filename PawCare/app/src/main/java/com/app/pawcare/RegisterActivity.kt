package com.app.pawcare

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val backButtonImageView: ImageView = findViewById(R.id.back)
        backButtonImageView.setOnClickListener {
            onBackPressed()
            finish()
        }
    }


}