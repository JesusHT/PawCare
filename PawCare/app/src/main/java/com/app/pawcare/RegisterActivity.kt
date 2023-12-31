package com.app.pawcare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.api.JsonPostQuery
import com.app.pawcare.config.Config
import com.app.pawcare.databinding.ActivityRegisterBinding
import com.app.pawcare.utils.Errors
import com.app.pawcare.utils.Messages
import com.app.pawcare.utils.Utils
import com.app.pawcare.utils.ValidateData
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

        Messages.setErrorView(b.errorMessage)
        Messages.setSuccessView(b.successMessage)

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

        b.termsCheckbox.setOnCheckedChangeListener { _, _ -> }

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
                        Messages.showError(Errors.ERROR_EMAIL_EXIST)
                    } else {
                        loadViewCaptcha(email, name, password)
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
        val inputType = Utils.showPassword(isPasswordVisible)
        b.password.inputType      = inputType
        b.confirmPasswd.inputType = inputType
    }
    private fun validateFields ( name: String, email: String, password: String, confirmPassword: String, termsAccepted: Boolean): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Messages.showError(Errors.ERROR_DATA_EMPTY)
            return false
        } else if (!ValidateData.isValidName(name)) {
            Messages.showError(Errors.ERROR_INVALID_NAME)
            return false
        } else if (!ValidateData.isValidEmail(email)) {
            Messages.showError(Errors.ERROR_INVALID_EMAIL)
            return false
        } else if (!ValidateData.isValidPassword(password)) {
            Messages.showError(Errors.ERROR_INVALID_PASSWORD)
            return false
        } else if (!ValidateData.isValidConfirmation(password, confirmPassword)) {
            Messages.showError(Errors.ERROR_PASSWORD_MISMATCH)
            return false
        } else if (!termsAccepted) {
            Messages.showError(Errors.ERROR_TERMS_NOT_ACCEPTED)
            return false
        }

        Messages.clearMessages()
        return true
    }
    private suspend fun validateEmail(email: String): Boolean{
        val postData = "email=$email"
        val result = JsonPostQuery(Config.URL_VALIDATE_USER, postData).execute()
        val jsonObject = JSONObject(result)

        return jsonObject.optBoolean("userExist")
    }
}

