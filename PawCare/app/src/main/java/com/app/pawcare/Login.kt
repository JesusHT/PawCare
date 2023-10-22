package com.app.pawcare

import android.os.AsyncTask

class Login(private val listener: LoginListener) : AsyncTask<Pair<String, String>, Void, Boolean>() {

    override fun doInBackground(vararg params: Pair<String, String>): Boolean {
        val email    = params[0].first
        val password = params[0].second

        val url = Config.URL_LOGIN
        val postParams = mapOf("email" to email, "pass" to password)

        return ApiQuery.sendPostRequest(url, postParams)
    }

    override fun onPostExecute(result: Boolean) {
        listener.onLoginResult(result)
    }
}
