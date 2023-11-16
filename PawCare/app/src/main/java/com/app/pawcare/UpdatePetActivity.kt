package com.app.pawcare

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityUpdatePetBinding
import com.app.pawcare.models.PetsTableModel
import com.app.pawcare.slqlite.PetsQueries
import com.app.pawcare.utils.Errors
import com.app.pawcare.utils.Messages
import java.util.Calendar

class UpdatePetActivity : AppCompatActivity() {
    private lateinit var b : ActivityUpdatePetBinding
    private lateinit var petsQueries: PetsQueries
    private var selectedImageUri: String? = null
    private var selectedPetType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityUpdatePetBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.back.setOnClickListener {
            loadHomeFragment()
        }

        Messages.setErrorView(b.errorMessage)

        petsQueries = PetsQueries(this)

        val sexSpinner = b.sexSpinner
        val sexOptions = resources.getStringArray(R.array.sex_options)

        val adapter = ArrayAdapter(this, R.layout.sex_spinner_item, sexOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sexSpinner.adapter = adapter

        val birthdayEditText = findViewById<EditText>(R.id.birthday)

        birthdayEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year  = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day   = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                birthdayEditText.setText(selectedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        b.dog.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedPetType = "Dog"
                b.typePetSelected.text = "Perro"

            }
        }

        b.cat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedPetType = "Cat"
                b.typePetSelected.text = "Gato"
            }
        }

        val petId = intent.getIntExtra("id", -1)
        loadFields(petId.toLong())
    }

    private fun loadFields(id: Long) {
        val cursor = petsQueries.getPetById(id)
        if (cursor.moveToFirst()) {
            val name     = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_NAME))
            val raza     = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_RAZA))
            val peso     = cursor.getInt(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_PESO))
            val sex      = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_SEX))
            val birthday = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_BIRTHDAY))
            val typePet  = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_TYPEPET))
            val photo    = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_PHOTO))

            b.namePet.setText(name)
            b.raza.setText(raza)
            b.peso.setText(peso.toString())
            b.birthday.setText(birthday)
            selectedPetType = typePet

            val txtTypePet = when (typePet) {
                "Cat" -> "Gato"
                "Dog" -> "Perro"
                else -> throw IllegalArgumentException("No found")
            }

            b.urlImage.text = photo
            b.typePetSelected.text = txtTypePet

            val sexOptions = resources.getStringArray(R.array.sex_options)
            val sexPosition = sexOptions.indexOf(sex)

            b.sexSpinner.setSelection(sexPosition)
        }

        cursor.close()
    }

    private fun validateFields(): Boolean {
        val name     = b.namePet.text.toString().trim()
        val raza     = b.raza.text.toString().trim()
        val pesoStr  = b.peso.text.toString().trim().toIntOrNull() ?: 0
        val birthday = b.birthday.text.toString().trim()
        val sex      = b.sexSpinner.selectedItem.toString()

        if (name.isEmpty() || birthday.isEmpty() || raza.isEmpty()) {
            Messages.showError(Errors.ERROR_DATA_EMPTY)
            return false
        }

        if (pesoStr <= 0) {
            Messages.showError(Errors.ERROR_INVALID_WEIGHT)
            return false
        }

        if (sex == resources.getStringArray(R.array.sex_options)[0]) {
            Messages.showError(Errors.ERROR_SELECT_SEX)
            return false
        }

        return true
    }

    private fun loadHomeFragment(){
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("goToFragment", "HomeFragment")
        startActivity(intent)
        finish()
    }
}