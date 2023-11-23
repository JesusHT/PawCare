    package com.app.pawcare

    import android.annotation.SuppressLint
    import android.app.AlarmManager
    import android.app.PendingIntent
    import android.content.Context
    import android.content.Intent
    import android.os.Build
    import android.os.Bundle
    import android.widget.ArrayAdapter
    import androidx.annotation.RequiresApi
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.app.NotificationManagerCompat
    import com.app.pawcare.R.string.update_reminder_title
    import com.app.pawcare.databinding.ActivityRemindersBinding
    import com.app.pawcare.interfaces.EventNotificationsManager
    import com.app.pawcare.slqlite.NotificationsQueries
    import com.app.pawcare.slqlite.PetsQueries
    import com.app.pawcare.users.SessionManager
    import com.app.pawcare.utils.Errors
    import com.app.pawcare.utils.Messages
    import com.app.pawcare.utils.NotificationReceiver
    import com.app.pawcare.utils.Utils
    import java.text.ParseException
    import java.text.SimpleDateFormat
    import java.time.LocalDate
    import java.time.format.DateTimeFormatter
    import java.util.Calendar
    import java.util.Locale

    class UpdateRemindersActivity  : AppCompatActivity()  {
        lateinit var b: ActivityRemindersBinding
        private var petNameIdMap = mutableMapOf<String, Int>()
        private var notificationId: Int = -1
        private lateinit var petsQueries: PetsQueries
        private lateinit var notificationsQueries: NotificationsQueries

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            b = ActivityRemindersBinding.inflate(layoutInflater)
            setContentView(b.root)

            notificationsQueries = NotificationsQueries(this)
            petsQueries          = PetsQueries(this)

            Messages.setErrorView(b.errorMessage)

            b.date.setOnClickListener { Utils.generateCalendar(b.date,this) }
            b.timer.setOnClickListener{ Utils.showTimePickerDialog(b.timer, this)}

            notificationId = intent.getIntExtra("notificationId", -1)

            if (notificationId != -1) {
                loadNotificationData()
                loadFields()
            } else {
                finish()
            }

            b.apply {
                back.setOnClickListener { finish() }
                cancel.setOnClickListener {
                    cancelNotification(notificationId)
                    notificationsQueries.deleteNotificationByPetId(notificationId.toLong())
                    EventNotificationsManager.onNotificationChangedListener?.invoke()
                    finish()
                }
                titleMain.text = resources.getString(update_reminder_title)
                b.cancel.text  = resources.getString(R.string.update_reminder_button_delete)
                save.setOnClickListener { updateNotification() }
            }
        }

        private fun loadNotificationData(){
            val cursor = petsQueries.getNamesPetByOwnerId(SessionManager.getUserInformation(this))

            val petNames = mutableListOf<String>()
            petNames.add(resources.getString(R.string.reminders_txt_spinner))

            while (cursor.moveToNext()) {
                val displayName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val id          = cursor.getInt(cursor.getColumnIndexOrThrow("idPet"))
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

        private fun loadFields() {
            val cursor = notificationsQueries.getNotificationsByID(notificationId.toLong())

            if (cursor.moveToFirst()) {
                val petId            = cursor.getInt(cursor.getColumnIndexOrThrow("idPet"))
                val title            = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val description      = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val typeNotification = cursor.getString(cursor.getColumnIndexOrThrow("typeNotification"))
                val date             = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val time             = cursor.getString(cursor.getColumnIndexOrThrow("time"))

                b.title.setText(title)
                b.description.setText(description)
                b.date.setText(date)
                b.timer.setText(time)

                val typeIndex = getIndexForType(typeNotification)
                b.typeNotification.setSelection(typeIndex)

                val position = petNameIdMap.values.indexOf(petId)

                if (position != -1) {
                    val spinnerPosition = position + 1
                    b.pets.setSelection(spinnerPosition)
                }
            }

            cursor.close()
        }

        private fun getIndexForType(type: String): Int {
            val remaindersOptions = resources.getStringArray(R.array.remainders_options)
            return remaindersOptions.indexOf(type)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun updateNotification() {
            if (validateFields()) {
                val petName = b.pets.selectedItem.toString()
                val petId = petNameIdMap[petName] ?: 0
                val title = b.title.text.toString().trim()
                val description = b.description.text.toString().trim()
                val typeNotification = b.typeNotification.selectedItem.toString()
                val date = b.date.text.toString().trim()
                val time = b.timer.text.toString().trim()

                val updatedRows = notificationsQueries.updateNotification(
                    notificationId.toLong(),
                    petId,
                    title,
                    description,
                    typeNotification,
                    date,
                    time
                )

                if (updatedRows > 0) {
                    setNotification(time, date, description, title, notificationId, typeNotification)
                    EventNotificationsManager.onNotificationChangedListener?.invoke()
                    finish()
                } else {
                    Messages.showError(Errors.ERROR_DB_NOTIFICATIONS)
                }
            }
        }


        @RequiresApi(Build.VERSION_CODES.O)
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

            if(!validateDate(date)){
                Messages.showError(Errors.ERROR_DATE)
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

        @RequiresApi(Build.VERSION_CODES.O)
        fun validateDate(date: String): Boolean {
            val currentDate = LocalDate.now()
            val formatter   = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val parsedDate  = LocalDate.parse(date, formatter)

            return parsedDate.isEqual(currentDate) || parsedDate.isAfter(currentDate)
        }

        @SuppressLint("ServiceCast", "ScheduleExactAlarm")
        private fun setNotification(time:String, dateN: String, message: String, title :String, id : Int, typeNotification: String){
            cancelNotification(id)

            val dateTimeString = "$time $dateN"
            val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())

            try {
                val date = dateFormat.parse(dateTimeString)

                if (date != null) {
                    val calendar = Calendar.getInstance()
                    calendar.time = date

                    val intent = Intent(this, NotificationReceiver::class.java)

                    intent.putExtra("notificationId", id)
                    intent.putExtra("message", message)
                    intent.putExtra("title", title)
                    intent.putExtra("typeNotification", typeNotification)

                    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        private fun cancelNotification(notificationId: Int) {
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.cancel(notificationId)
        }

    }