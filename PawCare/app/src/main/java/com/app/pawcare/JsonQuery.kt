package com.app.pawcare

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class JsonQuery(private val url: String, private val callback: (String) -> Unit) :
    AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void): String {
        var result = ""

        try {
            val url = URL(url)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result += line
            }
            reader.close()
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            result = "Error en la solicitud: " + e.message
        }

        return result
    }

    override fun onPostExecute(result: String) {
        callback(result)
    }
}