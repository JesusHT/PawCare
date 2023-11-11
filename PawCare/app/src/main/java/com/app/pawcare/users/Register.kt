package com.app.pawcare.users

import com.app.pawcare.config.Config
import com.app.pawcare.api.JsonPostQuery
import org.json.JSONObject

object Register {
    private var status: Boolean = false

    suspend fun createNewAccount(email: String, username: String, password: String){
        val postData   = "email=$email&pass=$password&name=$username"
        val result     = JsonPostQuery(Config.URL_REGISTER, postData).execute()
        val jsonObject = JSONObject(result)
        setStatus(jsonObject.optBoolean("status"))
    }
    private fun setStatus(status: Boolean){
        Register.status = status
    }
    fun getStatus(): Boolean{
        return status
    }
}