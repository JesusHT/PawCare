package com.app.pawcare.slqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.app.pawcare.models.PetsTableModel

class PetsQueries(context: Context) {

    private val dbHelper = PawCareDatabaseHelper(context)

    fun insertPet(name: String, raza: String, photo: String, peso: Int, sex: String, birthday: String, typePet: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(PetsTableModel.PetEntry.COLUMN_NAME, name)
            put(PetsTableModel.PetEntry.COLUMN_RAZA, raza)
            put(PetsTableModel.PetEntry.COLUMN_PHOTO, photo)
            put(PetsTableModel.PetEntry.COLUMN_PESO, peso)
            put(PetsTableModel.PetEntry.COLUMN_SEX, sex)
            put(PetsTableModel.PetEntry.COLUMN_BIRTHDAY, birthday)
            put(PetsTableModel.PetEntry.COLUMN_TYPEPET, typePet)
        }

        return db.insert(PetsTableModel.PetEntry.TABLE_NAME, null, values)
    }

    // SELECT PETS
    fun getAllPets(): Cursor {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            PetsTableModel.PetEntry.COLUMN_ID,
            PetsTableModel.PetEntry.COLUMN_NAME,
            PetsTableModel.PetEntry.COLUMN_RAZA,
            PetsTableModel.PetEntry.COLUMN_PHOTO,
            PetsTableModel.PetEntry.COLUMN_PESO,
            PetsTableModel.PetEntry.COLUMN_SEX,
            PetsTableModel.PetEntry.COLUMN_BIRTHDAY,
            PetsTableModel.PetEntry.COLUMN_TYPEPET
        )

        return db.query(
            PetsTableModel.PetEntry.TABLE_NAME,
            projection, null,null, null, null, null
        )
    }

    // SELECT PET BY ID
    fun getPetById(id: Long): Cursor {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            PetsTableModel.PetEntry.COLUMN_ID,
            PetsTableModel.PetEntry.COLUMN_NAME,
            PetsTableModel.PetEntry.COLUMN_RAZA,
            PetsTableModel.PetEntry.COLUMN_PHOTO,
            PetsTableModel.PetEntry.COLUMN_PESO,
            PetsTableModel.PetEntry.COLUMN_SEX,
            PetsTableModel.PetEntry.COLUMN_BIRTHDAY,
            PetsTableModel.PetEntry.COLUMN_TYPEPET
        )
        val selection = "${PetsTableModel.PetEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        return db.query(
            PetsTableModel.PetEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
    }

    // UPDATE PET BY ID
    fun updatePet(id: Long, name: String, raza: String, photo: String, peso: Int, sex: String, birthday: String, typePet: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(PetsTableModel.PetEntry.COLUMN_NAME, name)
            put(PetsTableModel.PetEntry.COLUMN_RAZA, raza)
            put(PetsTableModel.PetEntry.COLUMN_PHOTO, photo)
            put(PetsTableModel.PetEntry.COLUMN_PESO, peso)
            put(PetsTableModel.PetEntry.COLUMN_SEX, sex)
            put(PetsTableModel.PetEntry.COLUMN_BIRTHDAY, birthday)
            put(PetsTableModel.PetEntry.COLUMN_TYPEPET, typePet)
        }

        val selection = "${PetsTableModel.PetEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        return db.update(
            PetsTableModel.PetEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
    }

    // DELETE PET BY ID
    fun deletePet(id: Long): Int {
        val db = dbHelper.writableDatabase
        val selection = "${PetsTableModel.PetEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        return db.delete(PetsTableModel.PetEntry.TABLE_NAME, selection, selectionArgs)
    }
}
