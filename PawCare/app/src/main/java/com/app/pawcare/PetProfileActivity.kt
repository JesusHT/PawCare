package com.app.pawcare

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityPetProfileBinding
import com.app.pawcare.models.PetsTableModel
import com.app.pawcare.slqlite.PetsQueries


class PetProfileActivity : AppCompatActivity() {
    lateinit var b : ActivityPetProfileBinding
    private lateinit var petsQueries: PetsQueries
    private var petId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPetProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        petsQueries = PetsQueries(this)

        b.back.setOnClickListener { finish() }

        val id = intent.getIntExtra("id", -1)

        if (id != -1) {
            petId = id
            loadData(petId.toLong())
        } else {
            finish()
        }
    }

    private fun loadData(id: Long) {
        val cursor = petsQueries.getPetById(id)
        if (cursor.moveToFirst()) {
            val name     = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_NAME))
            val raza     = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_RAZA))
            val peso     = cursor.getInt(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_PESO))
            val sex      = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_SEX))
            val birthday = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_BIRTHDAY))
            val typePet  = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_TYPEPET))
            val photo    = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_PHOTO))

            b.title.text = "Recordatorios de $name"
        }

        cursor.close()
    }
}