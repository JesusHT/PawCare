package com.app.pawcare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.pawcare.api.JsonPostQuery
import com.app.pawcare.config.Config
import com.app.pawcare.databinding.ActivityCaptchaBinding
import com.app.pawcare.users.Register
import com.app.pawcare.utils.Errors
import com.app.pawcare.utils.Messages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class CaptchaActivity : AppCompatActivity() {
    lateinit var b : ActivityCaptchaBinding

    private var reSendCounter: Int = 0
    private var code:     String = ""
    private var email:    String = ""
    private var username: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCaptchaBinding.inflate(layoutInflater)
        setContentView(b.root)

        Messages.setErrorView(b.errorMessage)
        Messages.setSuccessView(b.successMessage)

        val typeAction  = intent.getStringExtra("typeAction")

        email = intent.getStringExtra("email").toString()
        b.email.text = email

        createNewCode()
        handleResendButton()

        b.validate.setOnClickListener {
            if (validateCaptcha()){
                if (typeAction.equals("Register")){
                    username = intent.getStringExtra("name").toString()
                    password = intent.getStringExtra("password").toString()

                    lifecycleScope.launch(Dispatchers.Main) {
                        val success = createAccount(email, username, password)
                        if (success) {
                            loadSuccessTemplateIntent("Register")
                        } else {
                            Messages.showError(Errors.ERROR_CREATE_ACCOUNT)
                        }
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val success = updatePassword(email)
                        if (success) {
                            loadSuccessTemplateIntent("Forgot")
                        } else {
                            Messages.showError(Errors.ERROR_UPDATE_PASSWORD)
                        }
                    }
                }
            } else {
                Messages.showError(Errors.ERROR_CAPTCHA)
            }
        }

        b.back.setOnClickListener {
            if (typeAction.equals("Register")){
                loadRegisterActivityIntent()
            } else {
                loadForgotActivityIntent()
            }
        }
    }

    private suspend fun createAccount(email: String, username: String, password: String) : Boolean {
        Register.createNewAccount(email,username,password)

        return Register.getStatus()
    }

    private suspend fun updatePassword(email: String): Boolean {
        val postData   = "email=$email"
        val result     = JsonPostQuery(Config.URL_CREATE_PASSWORD, postData).execute()
        val jsonObject = JSONObject(result)

        println(jsonObject.optString("password"))

        return jsonObject.optBoolean("status")
    }

    private fun validateCaptcha() : Boolean {
        val num1 = b.num1.text.toString()
        val num2 = b.num2.text.toString()
        val num3 = b.num3.text.toString()
        val num4 = b.num4.text.toString()

        return if (num1 == code[2].toString() &&
            num2 == code[4].toString() &&
            num3 == code[6].toString() &&
            num4 == code[8].toString()) {

            true
        } else {
            Messages.showError(Errors.ERROR_CAPTCHA)
            false
        }
    }

    private suspend fun createCode(email: String){
        val postData   = "email=$email"
        val result     = JsonPostQuery(Config.URL_CAPTCHA, postData).execute()

        code = result
        println(code)
    }

    private fun createNewCode(){
        lifecycleScope.launch(Dispatchers.Main) {
            createCode(email)
        }
    }

    private fun handleResendButton() {
        b.reload.setOnClickListener {
            if (reSendCounter < 3) {
                reSendCounter++
                createNewCode()
                Messages.showSuccess("Código reenviado ($reSendCounter de 3)")
            } else {
                Messages.showError("No se puede reenviar más códigos")
                b.reload.isEnabled = false
            }
        }
    }

    /*
        METHODS TO LOAD INTENT
    */
    private fun loadActivityIntent(destinationActivity: Class<*>) {
        val intent = Intent(this, destinationActivity)
        startActivity(intent)
        finish()
    }

    private fun loadRegisterActivityIntent() {
        loadActivityIntent(RegisterActivity::class.java)
    }

    private fun loadForgotActivityIntent() {
        loadActivityIntent(ForgotActivity::class.java)
    }

    private fun loadSuccessTemplateIntent(string: String){
        val intent = Intent(this, TemplateSuccess::class.java)
        intent.putExtra("typeAction", string)
        startActivity(intent)
        finish()
    }
}