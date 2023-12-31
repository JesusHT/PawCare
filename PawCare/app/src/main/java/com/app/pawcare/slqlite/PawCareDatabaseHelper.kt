package com.app.pawcare.slqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.app.pawcare.models.NotificationsTableModel
import com.app.pawcare.models.NotificationsTableModel.Companion.SQL_CREATE_NOTIFICATIONS_TABLE
import com.app.pawcare.models.PetsTableModel
import com.app.pawcare.models.PetsTableModel.Companion.SQL_CREATE_PETS_TABLE

class PawCareDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "pawcare.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_PETS_TABLE)
        db.execSQL(SQL_CREATE_NOTIFICATIONS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${PetsTableModel.PetEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${NotificationsTableModel.NotificationEntry.TABLE_NAME}")
        onCreate(db)
    }
}
