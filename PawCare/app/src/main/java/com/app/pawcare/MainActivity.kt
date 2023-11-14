package com.app.pawcare

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.pawcare.databinding.ActivityMainBinding
import com.app.pawcare.users.Login
import com.app.pawcare.users.SessionManager
import com.app.pawcare.utils.Errors
import com.app.pawcare.utils.Messages
import com.app.pawcare.utils.Utils
import com.app.pawcare.utils.ValidateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var b : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        Messages.setErrorView(b.errorMessage)
        Messages.setSuccessView(b.successMessage)

        if (SessionManager.isUserLoggedIn(this)) {
            loadHomeActivityIntent()
        }

        b.logIn.setOnClickListener {
            val email    = b.email.text.toString().trim()
            val password = b.password.text.toString().trim()

            if (validFields(email, password)) {
                val activityContext = this

                lifecycleScope.launch(Dispatchers.Main) {
                    val success = authenticateUser(email, password)
                    if (success) {
                        SessionManager.saveSessionState(activityContext, Login.getEmail(), Login.getUsername(), Login.getId())
                        loadHomeActivityIntent()
                    } else {
                        Messages.showError(Errors.ERROR_VALIDATE)
                        clearInputs()
                    }
                }
            }
        }

        b.forgot.setOnClickListener {
            loadForgotActivityIntent()
        }

        b.showPasswordCheckBox.setOnCheckedChangeListener { _, _ ->
            togglePasswordVisibility()
        }

        b.signUp.setOnClickListener {
            loadSignUpActivityIntent()
        }
    }

    private suspend fun authenticateUser(email: String, password: String): Boolean {
        Login.authenticate(email, password)
        return Login.getAccess()
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

    /*
        METHODS TO LOAD INTENT
    */
    private fun loadActivityIntent(destinationActivity: Class<*>) {
        val intent = Intent(this, destinationActivity)
        startActivity(intent)
        finish()
    }

    private fun loadHomeActivityIntent() {
        loadActivityIntent(HomeActivity::class.java)
    }

    private fun loadForgotActivityIntent() {
        loadActivityIntent(ForgotActivity::class.java)
    }

    private fun loadSignUpActivityIntent() {
        loadActivityIntent(RegisterActivity::class.java)
    }

    /*
        METHODS UTILS
    */
    private fun togglePasswordVisibility() {
        val isPasswordVisible = b.showPasswordCheckBox.isChecked
        val inputType = Utils.showPassword(isPasswordVisible)

        b.password.inputType = inputType
    }

    private fun clearInputs() {
        b.email.text.clear()
        b.password.text.clear()
    }
}
