package com.app.pawcare.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.PetProfileActivity
import com.app.pawcare.R
import com.app.pawcare.UpdatePetActivity
import com.app.pawcare.models.PetsModel
import com.app.pawcare.databinding.PetItemBinding
import com.app.pawcare.interfaces.OnItemChangedListener
import com.app.pawcare.slqlite.PetsQueries
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ViewPetsAdapter(private val list: List<PetsModel>,  private val listener: OnItemChangedListener) : RecyclerView.Adapter<ViewPetsAdapter.PetsViewHolder>() {
    class PetsViewHolder(private val b: PetItemBinding, private val context: Context, private val listener: OnItemChangedListener) : RecyclerView.ViewHolder(b.root) {

        @OptIn(DelicateCoroutinesApi::class)
        fun bind(pet: PetsModel) {
            b.Name.text = pet.name

            val textColorResource = when (pet.typePet) {
                "Cat" -> R.color.sky_blue
                "Dog" -> R.color.yellow
                else -> throw IllegalArgumentException("No found")
            }
            b.Name.setTextColor(ContextCompat.getColor(context, textColorResource))

            calculateAge(pet.birthday)

            val imageName = pet.photo
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val bitmap = loadImageAsync(context, imageName)
                    b.image.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    println("Error: $e")
                }
            }

            b.raza.text = HtmlCompat.fromHtml("<b>Raza: </b>${pet.raza}", HtmlCompat.FROM_HTML_MODE_LEGACY)
            b.peso.text = HtmlCompat.fromHtml("<b>Peso: </b>${pet.peso} kg", HtmlCompat.FROM_HTML_MODE_LEGACY)
            b.sex.text  = HtmlCompat.fromHtml("<b>Sexo: </b>${pet.sex}", HtmlCompat.FROM_HTML_MODE_LEGACY)

            b.btnMenu.setOnClickListener { v ->
                showContextMenu(v, pet.idPet)
            }

            b.image.setOnClickListener {
                initView(pet.idPet)
            }
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

        private fun showContextMenu(view: View, id: Int) {
                val popupMenu = PopupMenu(context, view)
                popupMenu.menuInflater.inflate(R.menu.contextual_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete -> {
                            showDeleteConfirmationDialog(id)
                            true
                        }
                        R.id.edit -> {
                            loadUpdatePetActivity(id)
                            true
                        }
                        R.id.see -> {
                            initView(id)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
        }

        private fun showDeleteConfirmationDialog(id: Int) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta mascota?")
                .setPositiveButton("Eliminar") { _, _ ->

                    val petsQueries = PetsQueries(context)
                    petsQueries.deletePet(id.toLong())
                    listener.onItemChanged()

                }.setNegativeButton("Cancelar") { _, _ ->
                    Log.d("DELETE PET",  "NO DELETE")
                }.show()
        }

        private fun initView(id: Int) {
            val intent = Intent(context, PetProfileActivity::class.java)
            intent.putExtra("id", id)
            context.startActivity(intent)
        }

        private fun loadUpdatePetActivity(id: Int) {
            val intent = Intent(context, UpdatePetActivity::class.java)
            intent.putExtra("id", id)
            context.startActivity(intent)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        val context = parent.context
        return PetsViewHolder(PetItemBinding.inflate(LayoutInflater.from(context), parent, false), context, listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
