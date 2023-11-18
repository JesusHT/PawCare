package com.app.pawcare.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.regex.Pattern
class ValidateData {
    companion object {
        fun isValidName(name: String): Boolean {
            val regex = "^[a-zA-Z0-9 ]+\$"
            return name.matches(Regex(regex))
        }

        fun isValidEmail(email: String): Boolean {
            val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"
            val pattern = Pattern.compile(emailRegex)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun isValidPassword(password: String): Boolean {
            val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*])[A-Za-z0-9!@#\$%^&*]{8,}\$"
            return password.matches(Regex(passwordRegex))
        }

        fun isValidConfirmation(password: String, confirmation: String): Boolean {
            return password == confirmation
        }

        @SuppressLint("SimpleDateFormat")
        fun isBirthDateValid(birthday: String): Boolean {
            val currentDate = Calendar.getInstance().time

            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val birthDate  = dateFormat.parse(birthday)

            return birthDate != null && !birthDate.after(currentDate)
        }

        fun validateFieldsPets(

        ){

        }

    }
}
