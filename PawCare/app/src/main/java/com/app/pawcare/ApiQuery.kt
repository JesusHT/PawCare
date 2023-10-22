package com.app.pawcare

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

object ApiQuery {

    fun sendPostRequest(urlString: String, params: Map<String, String>): Boolean {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true

            val postData = params.entries.joinToString(separator = "&") { (key, value) ->
                "$key=${value}"
            }

            val os: OutputStream = connection.outputStream
            val osw = OutputStreamWriter(os, "UTF-8")
            osw.write(postData)
            osw.flush()
            osw.close()
            os.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(inputStream)
                return jsonResponse.optBoolean("success")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
        return false
    }
}

