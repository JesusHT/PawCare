package com.app.pawcare

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class JsonQuery(private val url: String) {
    suspend fun execute(): String {
        return withContext(Dispatchers.IO) {
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

            result
        }
    }
}
