package com.app.pawcare

import org.json.JSONObject

object Login {
    private var access: Boolean = false
    private var email: String = ""
    private var username: String = ""
    private var id: Int = 0

    suspend fun authenticate(email: String, password: String) {
        val postData   = "email=$email&pass=$password"
        val result     = JsonPostQuery(Config.URL_LOGIN, postData).execute()
        val jsonObject = JSONObject(result)

        if (jsonObject.optBoolean("access")){
            setAccess(jsonObject.optBoolean("access"))
            setEmail(email)
            setUsername(jsonObject.optString("username"))
            setId(jsonObject.optInt("id"))
        } else {
            setAccess(jsonObject.optBoolean("access"))
        }
    }

    fun getEmail(): String {
        return email
    }

    fun getUsername(): String {
        return username
    }

    fun getId(): Int {
        return id
    }

    fun getAccess(): Boolean {
        return access
    }

    private fun setAccess(access: Boolean){
        this.access = access
    }

    private fun setEmail(email: String) {
        this.email = email
    }

    private fun setUsername(username: String) {
        this.username = username
    }

    private fun setId(id: Int) {
        this.id = id
    }
}
