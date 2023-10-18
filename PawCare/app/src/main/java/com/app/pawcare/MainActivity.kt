package com.app.pawcare

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var successTextView: TextView
    private lateinit var buttonLogin: Button
    private lateinit var showPasswordCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get references to UI elements
        emailEditText        = findViewById(R.id.email)
        passwordEditText     = findViewById(R.id.password)
        errorTextView        = findViewById(R.id.error_message)
        successTextView      = findViewById(R.id.success_message)
        buttonLogin          = findViewById(R.id.log_in)
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox)

        // Set references to messages
        MessageUtils.setErrorView(errorTextView)
        MessageUtils.setSuccessView(successTextView)

        // Set click listener for the login button
        buttonLogin.setOnClickListener {
            onLoginClick(it)
        }

        // Set click listener for the show password checkbox
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
        // Aquí puedes agregar el código para cambiar a la vista de registro
        // Por ejemplo, puedes iniciar una nueva actividad
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun togglePasswordVisibility() {
        val isPasswordVisible = showPasswordCheckBox.isChecked
        val inputType = if (isPasswordVisible) {
            // Show password
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            // Hide password
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        passwordEditText.inputType = inputType
    }

    fun onLoginClick(view: View) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validate empty fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            MessageUtils.showError(Errors.ERROR_DATA_EMPTY)
            return
        }

        // Validate email format using the function from ValidateData
        if (!ValidateData.isValidEmail(email)) {
            MessageUtils.showError(Errors.ERROR_EMAIL)
            return
        }

        // Simulate a login by comparing with local variables
        val userEmail = "user@user.com"
        val userPassword = "user123"

        when {
            email == userEmail && password == userPassword -> {
                MessageUtils.showSuccess("Login successful")
            }
            email != userEmail -> {
                MessageUtils.showError(Errors.ERROR_VALIDATE)
            }
            password != userPassword -> {
                MessageUtils.showError(Errors.ERROR_PASSWORD)
            }
        }

        clearInputs()
    }

    private fun clearInputs() {
        emailEditText.text.clear()
        passwordEditText.text.clear()
    }
}