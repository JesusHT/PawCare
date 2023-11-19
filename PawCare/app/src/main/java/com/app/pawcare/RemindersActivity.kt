package com.app.pawcare

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityRemindersBinding
import com.app.pawcare.interfaces.EventNotificationsManager
import com.app.pawcare.slqlite.NotificationsQueries
import com.app.pawcare.slqlite.PetsQueries
import com.app.pawcare.utils.Errors
import com.app.pawcare.utils.Messages
import com.app.pawcare.utils.Utils

class RemindersActivity : AppCompatActivity() {
    lateinit var b : ActivityRemindersBinding
    private var petNameIdMap = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRemindersBinding.inflate(layoutInflater)
        setContentView(b.root)

        Messages.setErrorView(b.errorMessage)

        b.apply {
            back.setOnClickListener  { finish() }
            cancel.setOnClickListener{ finish() }
            save.setOnClickListener  { saveNotification() }
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
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("idPet"))
            petNameIdMap[displayName] = id
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

    private fun saveNotification() {
        if (validateFields()) {
            val petName          = b.pets.selectedItem.toString()
            val petId            = petNameIdMap[petName] ?: 0
            val title            = b.title.text.toString().trim()
            val description      = b.description.text.toString().trim()
            val typeNotification = b.typeNotification.selectedItem.toString()
            val date             = b.date.text.toString().trim()
            val time             = b.timer.text.toString().trim()

            val notificationsQueries = NotificationsQueries(this)
            val newRowId = notificationsQueries.insertNotification(petId, title, description, typeNotification, date, time)

            if (newRowId > 0) {
                EventNotificationsManager.onNotificationChangedListener?.invoke()
                finish()
            } else {
                Messages.showError(Errors.ERROR_DB_NOTIFICATIONS)
            }
        }
    }

    private fun validateFields(): Boolean {
        val petName          = b.pets.selectedItem.toString()
        val title            = b.title.text.toString().trim()
        val description      = b.description.text.toString().trim()
        val typeNotification = b.typeNotification.selectedItem.toString()
        val date             = b.date.text.toString().trim()
        val time             = b.timer.text.toString().trim()

        if (petName.isEmpty() || title.isEmpty() || description.isEmpty() || typeNotification.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Messages.showError(Errors.ERROR_DATA_EMPTY)
            return false
        }

        if (typeNotification == resources.getStringArray(R.array.remainders_options)[0]) {
            Messages.showError(Errors.ERROR_SELECT_OPTION_REMINDER)
            return false
        }

        if (petName == resources.getString(R.string.reminders_txt_spinner)){
            Messages.showError(Errors.ERROR_SELECT_OPTION_PET)
            return false
        }

        return true
    }

    private fun getUserInformation() : Int {
        val sessionVars = getSharedPreferences("SessionVars", Context.MODE_PRIVATE)
        return sessionVars.getInt("id", 0)
    }


}