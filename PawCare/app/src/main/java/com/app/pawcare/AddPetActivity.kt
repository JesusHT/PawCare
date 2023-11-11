package com.app.pawcare

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityAddpetBinding
import java.util.Calendar

class AddPetActivity : AppCompatActivity() {
    private lateinit var b : ActivityAddpetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddpetBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.back.setOnClickListener {
            onBackPressed()
            finish()
        }

        val sexSpinner = b.sexSpinner
        val sexOptions = resources.getStringArray(R.array.sex_options)

        val adapter = ArrayAdapter(this, R.layout.sex_spinner_item, sexOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sexSpinner.adapter = adapter

        val birthdayEditText = findViewById<EditText>(R.id.birthday)

        birthdayEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year  = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day   = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                birthdayEditText.setText(selectedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

    }


}