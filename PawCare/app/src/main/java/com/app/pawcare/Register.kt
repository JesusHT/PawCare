package com.app.pawcare

import android.os.AsyncTask

class Register(private val listener: RegisterListener) : AsyncTask<Triple<String, String, String>, Void, Boolean>() {
    override fun doInBackground(vararg params: Triple<String, String, String>): Boolean {
        val email    = params[0].first
        val password = params[0].second
        val username = params[0].third

        val url = Config.URL_REGISTER
        val postParams = mapOf("username" to username,"email" to email, "pass" to password)

        return ApiQuery.sendPostRequest(url, postParams)
    }

    override fun onPostExecute(result: Boolean) {
        listener.onRegisterResult(result)
    }
}
