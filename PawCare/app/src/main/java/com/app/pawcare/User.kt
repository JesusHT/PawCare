package com.app.pawcare

import com.google.gson.Gson

data class User(
    val id: Int,
    val username: String,
    val email: String
)

fun jsonToUserModel(json: String): User? {
    try {
        val gson = Gson()
        return gson.fromJson(json, User::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}
