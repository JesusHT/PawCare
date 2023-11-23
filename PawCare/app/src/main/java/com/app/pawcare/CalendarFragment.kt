package com.app.pawcare

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.adapters.ViewCalendarNotificationsAdapter
import com.app.pawcare.databinding.FragmentCalendarBinding
import com.app.pawcare.interfaces.EventNotificationsManager
import com.app.pawcare.models.NotificationsModel
import com.app.pawcare.slqlite.NotificationsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarFragment : Fragment() {
    private lateinit var b: FragmentCalendarBinding
    private lateinit var notificationsQueries: NotificationsQueries

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentCalendarBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationsQueries = NotificationsQueries(requireContext())

        b.addReminder.setOnClickListener {
            val intent = Intent(requireActivity(), RemindersActivity::class.java)
            requireActivity().startActivity(intent)
        }

        val datePicker = b.calendar

        datePicker.init(
            datePicker.year,
            datePicker.month,
            datePicker.dayOfMonth
        ) { _, year, monthOfYear, dayOfMonth ->
            val dateSelected = "$dayOfMonth/${monthOfYear + 1}/$year"
            onNotificationDateChanged(dateSelected)
        }

        initNotificationView(getDate())
        EventNotificationsManager.onNotificationChangedListener = { onNotificationChanged() }
    }

    private fun initNotificationView(date : String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val notifications = getNotificationsFromDatabase(date)
            updateNotificationRecyclerView(notifications)
        }
    }

    private suspend fun getNotificationsFromDatabase(date : String): List<NotificationsModel> {
        return withContext(Dispatchers.IO) {
            val notificationsCursor = notificationsQueries.getNotificationsByDate(date)
            return@withContext parseNotificationsCursorToList(notificationsCursor)
        }
    }

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

            val notification = NotificationsModel(idNotification, idPet, title, description, typeNotification, date, time)
            notificationsList.add(notification)
        }

        cursor.close()
        return notificationsList
    }

    private fun updateNotificationRecyclerView(notifications: List<NotificationsModel>) {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.notificationRecyclerView)
        if (recyclerView.adapter == null) {
            val adapter = ViewCalendarNotificationsAdapter(notifications)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onNotificationChanged() {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.notificationRecyclerView)
        recyclerView.adapter = null
        initNotificationView(getDate())
    }

    private fun onNotificationDateChanged(date: String) {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.notificationRecyclerView)
        recyclerView.adapter = null
        initNotificationView(date)
    }

    private fun getDate() : String {
        val datePicker = b.calendar

        val defaultYear = datePicker.year
        val defaultMonth = datePicker.month
        val defaultDayOfMonth = datePicker.dayOfMonth

        return "$defaultDayOfMonth/${defaultMonth + 1}/$defaultYear"
    }
}