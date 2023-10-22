package com.app.pawcare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var showPasswordCheckBox: CheckBox
    private lateinit var passwordEditText: EditText
    private lateinit var comfirmPasswordEditText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var successTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        showPasswordCheckBox    = findViewById(R.id.showPasswordCheckBox)
        passwordEditText        = findViewById(R.id.password)
        comfirmPasswordEditText = findViewById(R.id.confirm_passwd)
        errorTextView           = findViewById(R.id.error_message)
        successTextView         = findViewById(R.id.success_message)

        showPasswordCheckBox.setOnCheckedChangeListener { _, _ ->
            togglePasswordVisibility()
        }

        MessageUtils.setErrorView(errorTextView)
        MessageUtils.setSuccessView(successTextView)

        val backButtonImageView: ImageView = findViewById(R.id.back)
        backButtonImageView.setOnClickListener {
            onBackPressed()
            finish()
        }

        val termsTextView: TextView = findViewById(R.id.terms_text)
        termsTextView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val url = Config.URL_TERMS
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        })

        val termsCheckBox = findViewById<CheckBox>(R.id.terms_checkbox)
        termsCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
            } else {
            }
        }
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
        comfirmPasswordEditText.inputType = inputType
    }

}
