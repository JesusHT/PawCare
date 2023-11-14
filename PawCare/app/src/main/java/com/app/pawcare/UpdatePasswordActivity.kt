package com.app.pawcare

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.pawcare.api.JsonPostQuery
import com.app.pawcare.config.Config
import com.app.pawcare.databinding.ActivityUpdatePasswordBinding
import com.app.pawcare.utils.Errors
import com.app.pawcare.utils.Messages
import com.app.pawcare.utils.Successes
import com.app.pawcare.utils.Utils
import com.app.pawcare.utils.ValidateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class UpdatePasswordActivity : AppCompatActivity() {
    private lateinit var b : ActivityUpdatePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(b.root)

        Messages.setErrorView(b.errorMessage)
        Messages.setSuccessView(b.successMessage)

        b.back.setOnClickListener {
            onBackPressed()
            finish()
        }
        b.showPasswordCheckBox.setOnCheckedChangeListener { _, _ -> togglePasswordVisibility() }
        b.save.setOnClickListener{
            val lastPassword     = b.lastPassword.text.toString()
            val newPassword      = b.newPassword.text.toString()
            val passwordConfirm  = b.confirmPasswd.text.toString()

            if (validateFields(lastPassword, newPassword, passwordConfirm)){
                lifecycleScope.launch(Dispatchers.Main) {
                    val success = validatePassword(lastPassword, getId())
                    if (success){
                        if(updatePassword(newPassword, getId())){
                            Messages.showSuccess(Successes.SUCCESS_UPDATE)
                            clearInptus()
                        } else {
                            Messages.showError(Errors.ERROR_UPDATE_PASSWORD)
                            clearInptus()
                        }
                    } else {
                        Messages.showError(Errors.ERROR_PASSWORD_VALIDATE)
                    }
                }
            }
        }
    }

    private fun validateFields (lastPassword: String, password: String, confirmPassword: String): Boolean {
        if (lastPassword.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Messages.showError(Errors.ERROR_DATA_EMPTY)
            return false
        } else if (!ValidateData.isValidPassword(password)) {
            Messages.showError(Errors.ERROR_INVALID_PASSWORD)
            return false
        } else if (!ValidateData.isValidConfirmation(password, confirmPassword)) {
            Messages.showError(Errors.ERROR_PASSWORD_MISMATCH)
            return false
        } else if (ValidateData.isValidConfirmation(lastPassword, confirmPassword)) {
            Messages.showError(Errors.ERROR_PASSWORD_SAME)
            return false
        }

        Messages.clearMessages()
        return true
    }

    private suspend fun updatePassword(password: String, id:Int): Boolean {
        val postData = "pass=$password&id=$id"
        val result = JsonPostQuery(Config.URL_UPDATE, postData).execute()
        val jsonObject = JSONObject(result)

        return jsonObject.optBoolean("status")
    }

    private suspend fun validatePassword(lastPassword: String, id:Int) : Boolean {
        val postData = "lastPassword=$lastPassword&id=$id"
        val result = JsonPostQuery(Config.URL_VALIDATE, postData).execute()
        val jsonObject = JSONObject(result)

        return jsonObject.optBoolean("status")
    }

    private fun getId(): Int {
        val sessionVars = getSharedPreferences("SessionVars", Context.MODE_PRIVATE)

        return sessionVars.getInt("id", 0)
    }

    private fun clearInptus(){
        b.lastPassword.text.clear()
        b.newPassword.text.clear()
        b.confirmPasswd.text.clear()
    }

    private fun togglePasswordVisibility() {
        val isPasswordVisible = b.showPasswordCheckBox.isChecked
        val inputType = Utils.showPassword(isPasswordVisible)
        b.newPassword.inputType   = inputType
        b.confirmPasswd.inputType = inputType
        b.lastPassword.inputType  = inputType
    }
}