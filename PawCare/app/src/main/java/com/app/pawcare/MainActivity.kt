package com.app.pawcare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.pawcare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LoginListener {

    private lateinit var b : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        if (isUserLoggedIn()) {
            loadViewHome()
        }

        MessageUtils.setErrorView(b.errorMessage)
        MessageUtils.setSuccessView(b.successMessage)

        b.logIn.setOnClickListener {
            onLoginClick(it)
        }

        b.showPasswordCheckBox.setOnCheckedChangeListener { _, _ ->
            togglePasswordVisibility()
        }

        b.signUp.setOnClickListener {
            onSignUpClick(it)
        }
    }

    fun onSignUpClick(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun onLoginClick(view: View) {
        val email    = b.email.text.toString().trim()
        val password = b.password.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            MessageUtils.showError(Errors.ERROR_DATA_EMPTY)
            clearInputs()
            return
        }

        if (!ValidateData.isValidEmail(email)) {
            MessageUtils.showError(Errors.ERROR_EMAIL)
            clearInputs()
            return
        }

        val loginTask = Login(this)
        loginTask.execute(Pair(email, password))
    }

    private fun togglePasswordVisibility() {
        val isPasswordVisible = b.showPasswordCheckBox.isChecked
        val inputType = if (isPasswordVisible) {
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
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

    override fun onLoginResult(success: Boolean) {
        if (success) {

            val email = b.email.text.toString().trim()
            val postData = "email=$email"

            JsonPostQuery(Config.URL_USER, postData) { result ->
                val userModel = jsonToUserModel(result)

                if (userModel != null) {
                    val id        = userModel.id
                    val username  = userModel.username
                    val email     = userModel.email

                    saveSessionState(true, email, username, id)
                }
            }.execute()

            loadViewHome()

        } else {
            MessageUtils.showError(Errors.ERROR_VALIDATE)
        }
    }

    private fun saveSessionState(isLoggedIn: Boolean, email: String, username: String, id: Int) {
        val SessionVars = getSharedPreferences("SessionVars", Context.MODE_PRIVATE)
        val editor = SessionVars.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.putString("email", email)
        editor.putString("username", username)
        editor.putInt("id",id)
        editor.apply()
    }

    private fun isUserLoggedIn(): Boolean {
        val SessionVars = getSharedPreferences("SessionVars", Context.MODE_PRIVATE)
        return SessionVars.getBoolean("isLoggedIn", false)
    }
}
