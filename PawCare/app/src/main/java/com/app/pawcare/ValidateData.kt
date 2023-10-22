package com.app.pawcare

import java.util.regex.Pattern

class ValidateData {
    companion object {
        fun isValidEmail(email: String): Boolean {
            val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"
            val pattern = Pattern.compile(emailRegex)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }


    }
}