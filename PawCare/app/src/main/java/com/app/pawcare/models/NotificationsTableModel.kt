package com.app.pawcare.models

import android.provider.BaseColumns

class NotificationsTableModel {
    object NotificationEntry : BaseColumns {
        const val TABLE_NAME               = "notifications"
        const val COLUMN_ID                = "idNotification"
        const val COLUMN_PET_ID            = "idPet"
        const val COLUMN_TITLE             = "title"
        const val COLUMN_DESCRIPTION       = "description"
        const val COLUMN_NOTIFICATION_TYPE = "typeNotification"
        const val COLUMN_DATE              = "date"
        const val COLUMN_TIME              = "time"
    }

    companion object {
        const val SQL_CREATE_NOTIFICATIONS_TABLE = """
            CREATE TABLE ${NotificationEntry.TABLE_NAME} (
                ${NotificationEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${NotificationEntry.COLUMN_PET_ID} INTEGER NOT NULL,
                ${NotificationEntry.COLUMN_TITLE} TEXT NOT NULL,
                ${NotificationEntry.COLUMN_DESCRIPTION} TEXT NOT NULL,
                ${NotificationEntry.COLUMN_NOTIFICATION_TYPE} TEXT NOT NULL,
                ${NotificationEntry.COLUMN_DATE} TEXT NOT NULL,
                ${NotificationEntry.COLUMN_TIME} TEXT NOT NULL
            )
        """
    }
}