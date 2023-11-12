package com.app.pawcare.utils

class Utils {
    companion object {
        fun showPassword(isChecked : Boolean) : Int {
            return if (isChecked) {
                android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }
}