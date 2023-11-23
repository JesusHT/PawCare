package com.app.pawcare

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.config.Config
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
            finish()
        }

        b.terms.setOnClickListener          { loadTermsView() }
        b.aboutUs.setOnClickListener        { loadAboutView() }
        b.logout.setOnClickListener         { logout() }
        b.updatePassword.setOnClickListener { loadUpdatePasswordActivityIntent() }
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
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

        finish()
    }

    private fun loadUpdatePasswordActivityIntent(){
        val intent = Intent(this, UpdatePasswordActivity::class.java)
        startActivity(intent)
    }

    private fun loadTermsView (){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Config.URL_TERMS))
        startActivity(intent)
    }

    private fun loadAboutView(){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Config.URL_ABOUT))
        startActivity(intent)
    }
}