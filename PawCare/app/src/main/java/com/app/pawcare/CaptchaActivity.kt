package com.app.pawcare

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityCaptchaBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CaptchaActivity : AppCompatActivity() {
    lateinit var b : ActivityCaptchaBinding
    private var reSendCounter: Int = 0
    private var code:     String = ""
    private var email:    String = ""
    private var username: String = ""
    private var password: String = ""

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCaptchaBinding.inflate(layoutInflater)
        setContentView(b.root)

        MessageUtils.setErrorView(b.errorMessage)
        MessageUtils.setSuccessView(b.successMessage)

        email    = intent.getStringExtra("email").toString()
        username = intent.getStringExtra("name").toString()
        password = intent.getStringExtra("password").toString()

        b.email.text = email;

        GlobalScope.launch(Dispatchers.Main) {
            createCode()
        }
        handleResendButton()

        b.validate.setOnClickListener {
            validateCaptcha()
        }
    }

    private suspend fun createCode(){

        val result = JsonQuery(Config.URL_TIPS).execute()
        code = result
        println(code)
    }

    private fun validateCaptcha() {
        val num1 = b.num1.text.toString()
        val num2 = b.num2.text.toString()
        val num3 = b.num3.text.toString()
        val num4 = b.num4.text.toString()

        if (num1 == code[2].toString() &&
            num2 == code[4].toString() &&
            num3 == code[6].toString() &&
            num4 == code[8].toString()) {

            createAccount(username, email, password)
        } else {
            MessageUtils.showError(Errors.ERROR_CAPTCHA)
        }
    }

    private fun createAccount(username: String, email: String, password: String){

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun handleResendButton() {
        b.reload.setOnClickListener {
            if (reSendCounter < 3) {
                reSendCounter++
                GlobalScope.launch(Dispatchers.Main) {
                    createCode()
                }
                MessageUtils.showSuccess("Código reenviado ($reSendCounter de 3)")
            } else {
                MessageUtils.showError("No se puede reenviar más códigos")
                b.reload.isEnabled = false
            }
        }
    }
}