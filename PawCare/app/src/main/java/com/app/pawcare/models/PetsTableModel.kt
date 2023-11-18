package com.app.pawcare.models

import android.provider.BaseColumns

class PetsTableModel {

    object PetEntry : BaseColumns {
        const val TABLE_NAME      = "pets"
        const val COLUMN_ID       = "idPet"
        const val COLUMN_OWNER_ID = "ownerId"
        const val COLUMN_NAME     = "name"
        const val COLUMN_RAZA     = "raza"
        const val COLUMN_PHOTO    = "photo"
        const val COLUMN_PESO     = "peso"
        const val COLUMN_SEX      = "sex"
        const val COLUMN_BIRTHDAY = "birthday"
        const val COLUMN_TYPEPET  = "typePet"
    }

    companion object {
        const val SQL_CREATE_PETS_TABLE = """
            CREATE TABLE ${PetEntry.TABLE_NAME} (
                ${PetEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${PetEntry.COLUMN_OWNER_ID} INTEGER NOT NULL,
                ${PetEntry.COLUMN_NAME} TEXT NOT NULL,
                ${PetEntry.COLUMN_RAZA} TEXT NOT NULL,
                ${PetEntry.COLUMN_PHOTO} TEXT NOT NULL,
                ${PetEntry.COLUMN_PESO} INTEGER NOT NULL,
                ${PetEntry.COLUMN_SEX} TEXT NOT NULL,
                ${PetEntry.COLUMN_BIRTHDAY} TEXT NOT NULL,
                ${PetEntry.COLUMN_TYPEPET} TEXT NOT NULL
            )
        """
    }
}
