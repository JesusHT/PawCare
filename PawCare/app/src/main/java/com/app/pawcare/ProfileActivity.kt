package com.app.pawcare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var b : ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.back.setOnClickListener {
            onBackPressed()
            finish()
        }

        b.logout.setOnClickListener { logout() }

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val sessionVars = getSharedPreferences("SessionVars", Context.MODE_PRIVATE)
        b.username.text = sessionVars.getString("email", null)
        b.email.text    = sessionVars.getString("username", null)
    }

    private fun logout() {
        val sessionVars = getSharedPreferences("SessionVars", Context.MODE_PRIVATE)
        val editor = sessionVars.edit()
        editor.remove("isLoggedIn")
        editor.remove("email")
        editor.remove("username")
        editor.remove("id")
        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}