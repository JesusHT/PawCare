package com.app.pawcare

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.adapters.ViewNotificationAdapter
import com.app.pawcare.databinding.ActivityNotificationsBinding
import com.app.pawcare.interfaces.EventNotificationsManager
import com.app.pawcare.models.NotificationsModel
import com.app.pawcare.slqlite.NotificationsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NotificationsActivity : AppCompatActivity() {
    private lateinit var b: ActivityNotificationsBinding
    private lateinit var notificationsQueries: NotificationsQueries
    private lateinit var notificationsAdapter: ViewNotificationAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(b.root)

        notificationsQueries = NotificationsQueries(this)

        b.back.setOnClickListener {
            val notificationId = intent.getIntExtra("notificationId", 0)
            if (notificationId != 0) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                finish()
            }
        }

        initNotificationView()
        cancelNotification()
        EventNotificationsManager.onNotificationChangedListener = { onNotificationChanged() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initNotificationView() {
        lifecycleScope.launch(Dispatchers.Main) {
            val notifications = getNotificationsFromDatabase()
            setupRecyclerView(notifications)

            val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(this@NotificationsActivity, notificationsAdapter))
            itemTouchHelper.attachToRecyclerView(b.notificationRecyclerView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getNotificationsFromDatabase(): List<NotificationsModel> {
        return withContext(Dispatchers.IO) {
            val notificationsCursor = notificationsQueries.getNotificationsByDate(getDate())
            return@withContext parseNotificationsCursorToList(notificationsCursor)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseNotificationsCursorToList(cursor: Cursor): List<NotificationsModel> {
        val notificationsList = mutableListOf<NotificationsModel>()

        while (cursor.moveToNext()) {
            val idNotification   = cursor.getInt(cursor.getColumnIndexOrThrow("idNotification"))
            val idPet            = cursor.getInt(cursor.getColumnIndexOrThrow("idPet"))
            val title            = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val description      = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val typeNotification = cursor.getString(cursor.getColumnIndexOrThrow("typeNotification"))
            val date             = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val time             = cursor.getString(cursor.getColumnIndexOrThrow("time"))

            if (isCurrentTimeEqualOrBefore(time)){
                val notification = NotificationsModel(idNotification, idPet, title, description, typeNotification, date, time)
                notificationsList.add(notification)
            }
        }

        cursor.close()
        return notificationsList
    }

    private fun setupRecyclerView(notifications: List<NotificationsModel>) {
        notificationsAdapter = ViewNotificationAdapter(this, notifications)
        b.notificationRecyclerView.adapter = notificationsAdapter
        b.notificationRecyclerView.layoutManager = LinearLayoutManager(this)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun onNotificationChanged() {
        val recyclerView: RecyclerView = this.findViewById(R.id.notificationRecyclerView)
        recyclerView.adapter = null
        initNotificationView()
    }

    private fun cancelNotification() {
        val notificationId = intent.getIntExtra("notificationId", 0)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(): String {
        val currentDate = LocalDate.now()
        val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return currentDate.format(format)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isCurrentTimeEqualOrBefore(givenTime: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = LocalTime.now()
        val givenLocalTime = LocalTime.parse(givenTime, formatter)

        return currentTime.isAfter(givenLocalTime)
    }
}
