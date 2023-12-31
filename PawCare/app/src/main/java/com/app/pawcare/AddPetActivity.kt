package com.app.pawcare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.ActivityAddpetBinding
import com.app.pawcare.interfaces.GlobalEventManager
import com.app.pawcare.slqlite.PetsQueries
import com.app.pawcare.users.SessionManager
import com.app.pawcare.utils.Errors
import com.app.pawcare.utils.Messages
import com.app.pawcare.utils.Utils
import com.app.pawcare.utils.ValidateData

class AddPetActivity : AppCompatActivity() {
    private lateinit var b : ActivityAddpetBinding
    private var selectedImageUri: String? = null
    private var selectedPetType: String? = null
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                handleGalleryResult(data)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddpetBinding.inflate(layoutInflater)
        setContentView(b.root)

        Messages.setErrorView(b.errorMessage)

        b.save.setOnClickListener     { savePet() }
        b.back.setOnClickListener     { finish() }
        b.birthday.setOnClickListener { Utils.generateCalendar(b.birthday,this) }
        b.addImage.setOnClickListener { openGallery() }

        val sexOptions = resources.getStringArray(R.array.sex_options)

        val adapter = ArrayAdapter(this, R.layout.sex_spinner_item, sexOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.sexSpinner.adapter = adapter

        b.dog.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedPetType = "Dog"
                b.typePetSelected.text = resources.getString(R.string.type_pet_dog)
            }
        }

        b.cat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedPetType = "Cat"
                b.typePetSelected.text = resources.getString(R.string.type_pet_cat)
            }
        }
    }

    private fun savePet() {
        if (validateFields() && selectedPetType != null) {
            val name     = b.namePet.text.toString().trim()
            val raza     = b.raza.text.toString().trim()
            val peso     = b.peso.text.toString().trim().toIntOrNull() ?: 0
            val birthday = b.birthday.text.toString().trim()
            val sex      = b.sexSpinner.selectedItem.toString()

            val petsQueries = PetsQueries(this)
            val newRowId = petsQueries.insertPet(SessionManager.getUserInformation(this),name, raza, selectedImageUri.toString(), peso, sex, birthday, selectedPetType!!)

            if (newRowId > 0) {
                GlobalEventManager.onPetUpdateListener?.invoke()
                finish()
            } else {
                Messages.showError(Errors.ERROR_DB_PETS)
            }
        }
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

        if (!ValidateData.isBirthDateValid(birthday)){
            Messages.showError(Errors.ERROR_BIRTHDAY)
            return false
        }

        if (pesoStr <= 0 || pesoStr >= 100) {
            Messages.showError(Errors.ERROR_INVALID_WEIGHT)
            return false
        }

        if (sex == resources.getStringArray(R.array.sex_options)[0]) {
            Messages.showError(Errors.ERROR_SELECT_SEX)
            return false
        }

        return true
    }

    // SAVE PHOTO

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    @SuppressLint("Range")
    private fun handleGalleryResult(data: Intent?) {
        if (data != null) {
            val selectedFileUri = data.data

            val cursor = contentResolver.query(selectedFileUri!!, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayName =
                        it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    b.urlImage.text = displayName
                    selectedImageUri = displayName

                    Utils.saveFileToLocal(selectedFileUri, displayName, this)
                }
            }
        }
    }
}
