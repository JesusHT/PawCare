package com.app.pawcare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityRegisterBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var b: ActivityRegisterBinding

    @OptIn(DelicateCoroutinesApi::class)
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
                GlobalScope.launch(Dispatchers.Main) {
                    val success = validateEmail(email)
                    if (success){
                        loadViewCaptcha(email, name, password)
                    } else {
                        MessageUtils.showError(Errors.ERROR_EMAIL_EXIST)
                    }
                }
            }
        }
    }

    private fun loadViewCaptcha(email:String, name:String, password: String){
        val intent = Intent(this, CaptchaActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("name", name)
        intent.putExtra("password", password)
        intent.putExtra("typeAction", "Register")
        startActivity(intent)
        finish()
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
    private fun validateFields ( name: String, email: String, password: String, confirmPassword: String, termsAccepted: Boolean): Boolean {
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

        MessageUtils.clearMessages()
        return true
    }
    private suspend fun validateEmail(email: String): Boolean{
        val postData = "email=$email"
        val result = JsonPostQuery(Config.URL_VALIDATE_USER, postData).execute()
        val jsonObject = JSONObject(result)

        return jsonObject.optBoolean("userExist")
    }
}

