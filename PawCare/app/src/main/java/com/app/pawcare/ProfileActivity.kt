package com.app.pawcare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityProfileBinding
import com.app.pawcare.users.SessionManager

class ProfileActivity : AppCompatActivity() {
    private lateinit var b : ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        getUserInformation()

        b.back.setOnClickListener {
            onBackPressed()
            finish()
        }

        b.logout.setOnClickListener { logout() }
    }

    private fun getUserInformation() {
        val sessionVars = getSharedPreferences("SessionVars", Context.MODE_PRIVATE)
        b.username.text = sessionVars.getString("email", null)
        b.email.text    = sessionVars.getString("username", null)
    }

    private fun logout(){
        SessionManager.logout(this)
        loadMainActivityIntent()
    }

    private fun loadMainActivityIntent(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}