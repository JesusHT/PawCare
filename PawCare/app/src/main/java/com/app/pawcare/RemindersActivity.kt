package com.app.pawcare

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityRemindersBinding
import com.app.pawcare.slqlite.PetsQueries
import com.app.pawcare.utils.Utils

class RemindersActivity : AppCompatActivity() {
    lateinit var b : ActivityRemindersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRemindersBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.apply {
           back.setOnClickListener { finish() }
        }
        b.date.setOnClickListener { Utils.generateCalendar(b.date,this) }
        b.timer.setOnClickListener{ Utils.showTimePickerDialog(b.timer, this)}

        loadSpinners()
    }

    private fun loadSpinners(){
        val petsQueries = PetsQueries(this)
        val cursor = petsQueries.getNamesPetByOwnerId(getUserInformation())

        val petNames = mutableListOf<String>()
        petNames.add(resources.getString(R.string.reminders_txt_spinner))

        while (cursor.moveToNext()) {
            val displayName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            petNames.add(displayName)
        }

        val adapter = ArrayAdapter(this, R.layout.sex_spinner_item, petNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.pets.adapter = adapter

        val remaindersOptions = resources.getStringArray(R.array.remainders_options)

        val adapterNotifications = ArrayAdapter(this, R.layout.sex_spinner_item, remaindersOptions)
        adapterNotifications.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.typeNotification.adapter = adapterNotifications
    }

    private fun getUserInformation() : Int {
        val sessionVars = getSharedPreferences("SessionVars", Context.MODE_PRIVATE)
        return sessionVars.getInt("id", 0)
    }


}