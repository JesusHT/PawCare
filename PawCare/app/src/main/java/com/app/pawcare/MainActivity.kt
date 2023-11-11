package com.app.pawcare

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityMainBinding
import com.app.pawcare.users.Login
import com.app.pawcare.utils.Errors
import com.app.pawcare.utils.Messages
import com.app.pawcare.utils.ValidateData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var b : ActivityMainBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        Messages.setErrorView(b.errorMessage)
        Messages.setSuccessView(b.successMessage)

        if (isUserLoggedIn()) {
            loadViewHome()
        }

        b.showPasswordCheckBox.setOnCheckedChangeListener { _, _ ->
            togglePasswordVisibility()
        }

        b.signUp.setOnClickListener {
            onSignUpClick()
        }

        b.logIn.setOnClickListener {
            val email    = b.email.text.toString().trim()
            val password = b.password.text.toString().trim()

            if (validFields(email, password)) {
                GlobalScope.launch(Dispatchers.Main) {
                    val success = authenticateUser(email, password)
                    if (success) {
                        saveSessionState(true, Login.getEmail(), Login.getUsername(), Login.getId())
                        loadViewHome()
                    } else {
                        Messages.showError(Errors.ERROR_VALIDATE)
                        clearInputs()
                    }
                }
            }
        }
    }

    fun onSignUpClick() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun validFields(email: String, password: String) : Boolean{
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Messages.showError(Errors.ERROR_DATA_EMPTY)
            clearInputs()
            return false
        }

        if (!ValidateData.isValidEmail(email)) {
            Messages.showError(Errors.ERROR_EMAIL)
            clearInputs()
            return false
        }

        return true
    }
    private fun saveSessionState(isLoggedIn: Boolean, email: String, username: String, id: Int) {
        val sessionVars = getSharedPreferences("SessionVars", MODE_PRIVATE)
        val editor = sessionVars.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.putString("email", email)
        editor.putString("username", username)
        editor.putInt("id",id)
        editor.apply()
    }
    private fun isUserLoggedIn(): Boolean {
        val sessionVars = getSharedPreferences("SessionVars", MODE_PRIVATE)
        return sessionVars.getBoolean("isLoggedIn", false)
    }
    private fun togglePasswordVisibility() {
        val isPasswordVisible = b.showPasswordCheckBox.isChecked
        val inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        b.password.inputType = inputType
    }
    private fun clearInputs() {
        b.email.text.clear()
        b.password.text.clear()
    }
    private fun loadViewHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
    private suspend fun authenticateUser(email: String, password: String): Boolean {
        Login.authenticate(email, password)
        return Login.getAccess()
    }
}
