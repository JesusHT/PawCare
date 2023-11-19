package com.app.pawcare.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.EditText
import com.app.pawcare.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

            val datePickerDialog = DatePickerDialog(context,
                R.style.DateTimePicker,
                { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                birthdayEditText.setText(selectedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        fun showTimePickerDialog(timer : EditText, context: Context) {
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                context,
                R.style.DateTimePicker,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)

                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    timer.setText(timeFormat.format(calendar.time))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )

            timePickerDialog.show()
        }

    }
}