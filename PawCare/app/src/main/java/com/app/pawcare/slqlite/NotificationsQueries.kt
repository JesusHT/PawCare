package com.app.pawcare.slqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.app.pawcare.models.NotificationsTableModel

class NotificationsQueries(context: Context) {

    private val dbHelper = PawCareDatabaseHelper(context)

    fun insertNotification(idPet: Int, title: String, description: String, typeNotification: String, date: String, time: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotificationsTableModel.NotificationEntry.COLUMN_PET_ID, idPet)
            put(NotificationsTableModel.NotificationEntry.COLUMN_TITLE, title)
            put(NotificationsTableModel.NotificationEntry.COLUMN_DESCRIPTION, description)
            put(NotificationsTableModel.NotificationEntry.COLUMN_NOTIFICATION_TYPE, typeNotification)
            put(NotificationsTableModel.NotificationEntry.COLUMN_DATE, date)
            put(NotificationsTableModel.NotificationEntry.COLUMN_TIME, time)
        }

        return db.insert(NotificationsTableModel.NotificationEntry.TABLE_NAME, null, values)
    }

    // SELECT NOTIFICATIONS
    fun getAllNotifications(): Cursor {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            NotificationsTableModel.NotificationEntry.COLUMN_ID,
            NotificationsTableModel.NotificationEntry.COLUMN_PET_ID,
            NotificationsTableModel.NotificationEntry.COLUMN_TITLE,
            NotificationsTableModel.NotificationEntry.COLUMN_DESCRIPTION,
            NotificationsTableModel.NotificationEntry.COLUMN_NOTIFICATION_TYPE,
            NotificationsTableModel.NotificationEntry.COLUMN_DATE,
            NotificationsTableModel.NotificationEntry.COLUMN_TIME
        )

        return db.query(
            NotificationsTableModel.NotificationEntry.TABLE_NAME,
            projection, null, null, null, null, null
        )
    }

    // SELECT NOTIFICATIONS BY DATE
    fun getNotificationsByDate(date: String): Cursor {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            NotificationsTableModel.NotificationEntry.COLUMN_ID,
            NotificationsTableModel.NotificationEntry.COLUMN_PET_ID,
            NotificationsTableModel.NotificationEntry.COLUMN_TITLE,
            NotificationsTableModel.NotificationEntry.COLUMN_DESCRIPTION,
            NotificationsTableModel.NotificationEntry.COLUMN_NOTIFICATION_TYPE,
            NotificationsTableModel.NotificationEntry.COLUMN_DATE,
            NotificationsTableModel.NotificationEntry.COLUMN_TIME
        )
        val selection = "${NotificationsTableModel.NotificationEntry.COLUMN_DATE} = ?"
        val selectionArgs = arrayOf(date)

        return db.query(
            NotificationsTableModel.NotificationEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
    }

    // SELECT NOTIFICATIONS BY PET ID
    fun getNotificationsByPetId(petId: Int): Cursor {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            NotificationsTableModel.NotificationEntry.COLUMN_ID,
            NotificationsTableModel.NotificationEntry.COLUMN_PET_ID,
            NotificationsTableModel.NotificationEntry.COLUMN_TITLE,
            NotificationsTableModel.NotificationEntry.COLUMN_DESCRIPTION,
            NotificationsTableModel.NotificationEntry.COLUMN_NOTIFICATION_TYPE,
            NotificationsTableModel.NotificationEntry.COLUMN_DATE,
            NotificationsTableModel.NotificationEntry.COLUMN_TIME
        )
        val selection = "${NotificationsTableModel.NotificationEntry.COLUMN_PET_ID} = ?"
        val selectionArgs = arrayOf(petId.toString())

        return db.query(
            NotificationsTableModel.NotificationEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
    }

    // UPDATE NOTIFICATION BY ID
    fun updateNotification(id: Long, idPet: Int, title: String, description: String, typeNotification: String, date: String, time: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotificationsTableModel.NotificationEntry.COLUMN_PET_ID, idPet)
            put(NotificationsTableModel.NotificationEntry.COLUMN_TITLE, title)
            put(NotificationsTableModel.NotificationEntry.COLUMN_DESCRIPTION, description)
            put(NotificationsTableModel.NotificationEntry.COLUMN_NOTIFICATION_TYPE, typeNotification)
            put(NotificationsTableModel.NotificationEntry.COLUMN_DATE, date)
            put(NotificationsTableModel.NotificationEntry.COLUMN_TIME, time)
        }

        val selection = "${NotificationsTableModel.NotificationEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        return db.update(
            NotificationsTableModel.NotificationEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
    }

    // DELETE NOTIFICATION BY ID
    fun deleteNotification(id: Long): Int {
        val db = dbHelper.writableDatabase
        val selection = "${NotificationsTableModel.NotificationEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        return db.delete(NotificationsTableModel.NotificationEntry.TABLE_NAME, selection, selectionArgs)
    }
}