package com.app.pawcare.users

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

object SessionManager {
    fun saveSessionState(context: Context, email: String, username: String, id: Int) {
        val sessionVars = context.getSharedPreferences("SessionVars", AppCompatActivity.MODE_PRIVATE)
        val editor = sessionVars.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("email", email)
        editor.putString("username", username)
        editor.putInt("id",id)
        editor.apply()
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val sessionVars = context.getSharedPreferences("SessionVars", AppCompatActivity.MODE_PRIVATE)
        return sessionVars.getBoolean("isLoggedIn", false)
    }

   fun logout(context: Context) {
        val sessionVars = context.getSharedPreferences("SessionVars", Context.MODE_PRIVATE)
        val editor = sessionVars.edit()
        editor.remove("isLoggedIn")
        editor.remove("email")
        editor.remove("username")
        editor.remove("id")
        editor.apply()
    }

    fun getUserInformation(context: Context) : Int {
        val sessionVars = context.getSharedPreferences("SessionVars", Context.MODE_PRIVATE)
        return sessionVars.getInt("id", 0)
    }
}