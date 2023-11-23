package com.app.pawcare

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.adapters.ViewCalendarNotificationsAdapter
import com.app.pawcare.databinding.ActivityPetProfileBinding
import com.app.pawcare.interfaces.EventNotificationsManager
import com.app.pawcare.interfaces.GlobalEventManager
import com.app.pawcare.models.NotificationsModel
import com.app.pawcare.models.PetsTableModel
import com.app.pawcare.slqlite.NotificationsQueries
import com.app.pawcare.slqlite.PetsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class PetProfileActivity : AppCompatActivity() {
    lateinit var b : ActivityPetProfileBinding
    private lateinit var petsQueries: PetsQueries
    private lateinit var notificationsQueries: NotificationsQueries
    private var petId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPetProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        petsQueries = PetsQueries(this)
        notificationsQueries = NotificationsQueries(this)

        b.back.setOnClickListener { finish() }
        b.addReminder.setOnClickListener {
            val intent = Intent(this, RemindersActivity::class.java)
            intent.putExtra("petId", petId)
            startActivity(intent)
        }

        val id = intent.getIntExtra("id", -1)

        if (id != -1) {
            petId = id
            loadData(petId.toLong())
        } else {
            finish()
        }

        b.menu.setOnClickListener { v ->
            showContextMenu(v,id)
        }

        initNotificationView()
        EventNotificationsManager.onNotificationChangedListener = { onNotificationChanged() }
    }

    private fun showContextMenu(view: View, id: Int) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_pets, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> {
                    showDeleteConfirmationDialog(id)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showDeleteConfirmationDialog(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar esta mascota? Ten en cuenta que también se eliminarán sus recordatorios. Esta acción es irreversible.")
            .setPositiveButton("Eliminar") { _, _ ->

                val petsQueries = PetsQueries(this)
                val notificationsQueries = NotificationsQueries(this)
                petsQueries.deletePet(id.toLong())
                notificationsQueries.deleteNotificationByPetId(id.toLong())
                GlobalEventManager.onPetUpdateListener?.invoke()
                finish()

            }.setNegativeButton("Cancelar") { _, _ ->
                Log.d("DELETE PET",  "NO DELETE")
            }.show()

        val positiveButton = builder.show().getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.error))
    }

    @SuppressLint("SetTextI18n")
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

            val typeIconResource = when (typePet) {
                "Cat" -> R.drawable.icon_cat
                "Dog" -> R.drawable.icon_dog
                else -> throw IllegalArgumentException("No found")
            }
            b.iconPet.setImageResource(typeIconResource)

            val textColorResource = when (typePet) {
                "Cat" -> R.color.sky_blue
                "Dog" -> R.color.yellow
                else -> throw IllegalArgumentException("No found")
            }
            b.titleReminders.setTextColor(ContextCompat.getColor(this, textColorResource))

            calculateAge(birthday)

            val context = this

            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val bitmap = loadImageAsync(context, photo)
                    b.image.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    println("Error: $e")
                }
            }

            b.raza.text = HtmlCompat.fromHtml("<b>Raza: </b>${raza}", HtmlCompat.FROM_HTML_MODE_LEGACY)
            b.peso.text = HtmlCompat.fromHtml("<b>Peso: </b>${peso} kg", HtmlCompat.FROM_HTML_MODE_LEGACY)
            b.sex.text  = HtmlCompat.fromHtml("<b>Sexo: </b>${sex}", HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        cursor.close()
    }

    private suspend fun loadImageAsync(context: Context, imageName: String): Bitmap = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val dirDocument = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val route = File(dirDocument, imageName)

            if (route.exists()) {
                try {
                    val fileInputStream = FileInputStream(route)
                    val bitmap = BitmapFactory.decodeStream(fileInputStream)
                    continuation.resume(bitmap)
                } catch (e: Exception) { continuation.resumeWithException(e) }
            } else { continuation.resumeWithException(IllegalArgumentException("Error: No found")) }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun calculateAge(birthday: String) {
        val currentDate = Calendar.getInstance().time

        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val birthDate = dateFormat.parse(birthday)

        val differenceInMillis = currentDate.time - birthDate!!.time
        val differenceInDays = (differenceInMillis / (24 * 60 * 60 * 1000)).toInt()

        val years = differenceInDays / 365
        val months = (differenceInDays % 365) / 30
        val days = differenceInDays % 30

        val date = when {
            years  > 0 && months > 0 -> "$years años y $months meses"
            months > 0 && days > 0 -> "$months meses y $days días"
            days > 0 -> "$days días"
            else -> "0 dias"
        }

        val formattedAge = if (date.isNotEmpty()) {
            "<b>Edad: </b>$date"
        } else {
            "<b>Edad: </b>No disponible"
        }

        b.age.text = HtmlCompat.fromHtml(formattedAge, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun initNotificationView() {
        lifecycleScope.launch(Dispatchers.Main) {
            val notifications = getNotificationsFromDatabase()
            updateNotificationRecyclerView(notifications)
        }
    }

    private suspend fun getNotificationsFromDatabase(): List<NotificationsModel> {
        return withContext(Dispatchers.IO) {
            val notificationsCursor = notificationsQueries.getNotificationsByPetId(petId)
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
        val recyclerView: RecyclerView = this.findViewById(R.id.notificationRecyclerView)
        if (recyclerView.adapter == null) {
            val adapter = ViewCalendarNotificationsAdapter(notifications)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun onNotificationChanged() {
        val recyclerView: RecyclerView = this.findViewById(R.id.notificationRecyclerView)
        recyclerView.adapter = null
        initNotificationView()
    }
}