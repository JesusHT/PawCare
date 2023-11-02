package com.app.pawcare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityRegisterBinding
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var b: ActivityRegisterBinding
    private var userExists: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        MessageUtils.setErrorView(b.errorMessage)
        MessageUtils.setSuccessView(b.successMessage)

        b.showPasswordCheckBox.setOnCheckedChangeListener { _, _ ->
            togglePasswordVisibility()
        }

        b.back.setOnClickListener {
            onBackPressed()
            finish()
        }

        b.termsText.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Config.URL_TERMS))
            startActivity(intent)
        }

        b.termsCheckbox.setOnCheckedChangeListener { _, _ ->

        }

        b.save.setOnClickListener {
            val name            = b.name.text.toString()
            val email           = b.email.text.toString()
            val password        = b.password.text.toString()
            val confirmPassword = b.confirmPasswd.text.toString()
            val termsAccepted   = b.termsCheckbox.isChecked

            if (validateFields(name, email, password, confirmPassword, termsAccepted)) {
                MessageUtils.showSuccess("Salio con madre")
                //val intent = Intent(this, CaptchaActivity::class.java)
                //intent.putExtra("email", email)
                //intent.putExtra("name", name)
                //intent.putExtra("password", password)
                //startActivity(intent)
            }
        }
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
        b.password.inputType      = inputType
        b.confirmPasswd.inputType = inputType
    }

    private fun validateFields (
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        termsAccepted: Boolean
    ): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            MessageUtils.showError(Errors.ERROR_DATA_EMPTY)
            return false
        } else if (!ValidateData.isValidName(name)) {
            MessageUtils.showError(Errors.ERROR_INVALID_NAME)
            return false
        } else if (!ValidateData.isValidEmail(email)) {
            MessageUtils.showError(Errors.ERROR_INVALID_EMAIL)
            return false
        } else if (!ValidateData.isValidPassword(password)) {
            MessageUtils.showError(Errors.ERROR_INVALID_PASSWORD)
            return false
        } else if (!ValidateData.isValidConfirmation(password, confirmPassword)) {
            MessageUtils.showError(Errors.ERROR_PASSWORD_MISMATCH)
            return false
        } else if (!termsAccepted) {
            MessageUtils.showError(Errors.ERROR_TERMS_NOT_ACCEPTED)
            return false
        }

        validateEmail(email)

        if (userExists){
            MessageUtils.showError(Errors.ERROR_EMAIL_EXIST)
            return false
        }

        MessageUtils.clearMessages()
        return true
    }

    private fun validateEmail(email: String) {
        val postData = "email=$email"

        JsonPostQuery(Config.URL_VALIDATE_USER, postData) { result ->
            val jsonObject = JSONObject(result)
            userExists = jsonObject.optBoolean("userExist")
        }.execute()
    }

}

