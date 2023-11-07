package com.app.pawcare

import android.content.Intent
import android.os.Bundle
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

                    GlobalScope.launch(Dispatchers.Main) {
                        val success = createAccount(email, username, password)
                        if (success) {
                            loadSuccessView()
                            MessageUtils.showSuccess("Cuenta creada con exito")
                        } else {
                            MessageUtils.showError(Errors.ERROR_CREATE_ACCOUNT)
                        }
                    }
                } else {
                    MessageUtils.showSuccess("Olvido la cotraseña")
                }
            } else {
                MessageUtils.showError(Errors.ERROR_CAPTCHA)
            }
        }

        b.back.setOnClickListener {
            if (typeAction.equals("Register")){
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, RemindersActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private suspend fun createAccount(email: String, username: String, password: String) : Boolean{
        Register.createNewAccount(email,username,password)

        return Register.getStatus()
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
            MessageUtils.showError(Errors.ERROR_CAPTCHA)
            false
        }
    }
    private fun handleResendButton() {
        b.reload.setOnClickListener {
            if (reSendCounter < 3) {
                reSendCounter++
                createNewCode()
                MessageUtils.showSuccess("Código reenviado ($reSendCounter de 3)")
            } else {
                MessageUtils.showError("No se puede reenviar más códigos")
                b.reload.isEnabled = false
            }
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun createNewCode(){
        GlobalScope.launch(Dispatchers.Main) {
            createCode(email)
        }
    }
    private suspend fun createCode(email: String){
        val postData   = "email=$email"
        val result     = JsonPostQuery(Config.URL_CAPTCHA, postData).execute()

        code = result
        println(code)
    }

    private fun loadSuccessView(){
        val intent = Intent(this, TemplateSuccess::class.java)
        intent.putExtra("typeAction", "Register")
        startActivity(intent)
        finish()
    }
}