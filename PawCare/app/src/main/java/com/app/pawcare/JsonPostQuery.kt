package com.app.pawcare

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class JsonPostQuery(private val url: String, private val postData: String, private val callback: (String) -> Unit) :
    AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void): String {
        var result = ""

        try {
            val url = URL(url)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true

            val postDataBytes = postData.toByteArray(StandardCharsets.UTF_8)
            connection.setRequestProperty("Content-Length", postDataBytes.size.toString())

            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.write(postDataBytes)
            outputStream.flush()
            outputStream.close()

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
