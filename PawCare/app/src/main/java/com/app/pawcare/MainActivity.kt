package com.app.pawcare

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), LoginListener {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var successTextView: TextView
    private lateinit var buttonLogin: Button
    private lateinit var showPasswordCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText        = findViewById(R.id.email)
        passwordEditText     = findViewById(R.id.password)
        errorTextView        = findViewById(R.id.error_message)
        successTextView      = findViewById(R.id.success_message)
        buttonLogin          = findViewById(R.id.log_in)
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox)

        MessageUtils.setErrorView(errorTextView)
        MessageUtils.setSuccessView(successTextView)

        buttonLogin.setOnClickListener {
            onLoginClick(it)
        }

        showPasswordCheckBox.setOnCheckedChangeListener { _, _ ->
            togglePasswordVisibility()
        }

        // Set click listener for the "Regístrate aquí" TextView
        val signUpTextView: TextView = findViewById(R.id.signUp)
        signUpTextView.setOnClickListener {
            onSignUpClick(it)
        }
    }

    fun onSignUpClick(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }


    fun onLoginClick(view: View) {
        val email    = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            MessageUtils.showError(Errors.ERROR_DATA_EMPTY)
            return
        }

        // Validate email format using the function from ValidateData
        if (!ValidateData.isValidEmail(email)) {
            MessageUtils.showError(Errors.ERROR_EMAIL)
            return
        }

        val loginTask = Login(this)
        loginTask.execute(Pair(email, password))

        clearInputs()
    }

    private fun togglePasswordVisibility() {
        val isPasswordVisible = showPasswordCheckBox.isChecked
        val inputType = if (isPasswordVisible) {
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        passwordEditText.inputType = inputType
    }

    private fun clearInputs() {
        emailEditText.text.clear()
        passwordEditText.text.clear()
    }

    override fun onLoginResult(success: Boolean) {
        if (success) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            MessageUtils.showError(Errors.ERROR_VALIDATE)
        }
    }
}
