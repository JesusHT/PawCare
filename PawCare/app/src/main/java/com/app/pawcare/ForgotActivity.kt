package com.app.pawcare

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.api.JsonPostQuery
import com.app.pawcare.config.Config
import com.app.pawcare.databinding.ActivityForgotBinding
import com.app.pawcare.utils.Errors
import com.app.pawcare.utils.Messages
import com.app.pawcare.utils.ValidateData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class ForgotActivity : AppCompatActivity() {
    private lateinit var b : ActivityForgotBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(b.root)

        Messages.setErrorView(b.errorMessage)

        b.back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        b.next.setOnClickListener {
            val email = b.email.text.toString().trim()

            if (validFields(email)) {
                GlobalScope.launch(Dispatchers.Main) {
                    val success = validateEmail(email)
                    if (success){
                        loadViewCaptcha(email)
                    } else {
                        Messages.showError(Errors.ERROR_EMAIL_NO_EXIST)
                    }
                }
            }
        }

    }

    private suspend fun validateEmail(email: String): Boolean{
        val postData = "email=$email"
        val result = JsonPostQuery(Config.URL_VALIDATE_USER, postData).execute()
        val jsonObject = JSONObject(result)

        return jsonObject.optBoolean("userExist")
    }

    private fun validFields(email: String) : Boolean {
        if (TextUtils.isEmpty(email)) {
            Messages.showError(Errors.ERROR_DATA_EMPTY)
            clearInput()
            return false
        }

        if (!ValidateData.isValidEmail(email)) {
            Messages.showError(Errors.ERROR_EMAIL)
            clearInput()
            return false
        }

        return true
    }

    private fun loadViewCaptcha(email:String){
        val intent = Intent(this, CaptchaActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("typeAction", "Forgot")
        startActivity(intent)
        finish()
    }

    private fun clearInput() {
        b.email.text.clear()
    }
}