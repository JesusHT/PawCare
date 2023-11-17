package com.app.pawcare.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.EditText
import java.util.Calendar

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

        fun generateCalendar(birthdayEditText : EditText, context: Context){
            val calendar = Calendar.getInstance()
            val year  = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day   = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                birthdayEditText.setText(selectedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

    }
}