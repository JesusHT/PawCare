package com.app.pawcare.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.R
import com.app.pawcare.models.PetsModel
import com.app.pawcare.databinding.PetItemBinding
import com.app.pawcare.slqlite.PetsQueries
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ViewPetsAdapter(private val list: List<PetsModel>) : RecyclerView.Adapter<ViewPetsAdapter.PetsViewHolder>() {
    class PetsViewHolder(private val b: PetItemBinding, private val context: Context) : RecyclerView.ViewHolder(b.root) {

        @OptIn(DelicateCoroutinesApi::class)
        fun bind(pet: PetsModel) {
            b.Name.text = pet.name

            val textColorResource = when (pet.typePet) {
                "Cat" -> R.color.sky_blue
                "Dog" -> R.color.yellow
                else -> throw IllegalArgumentException("No found")
            }
            b.Name.setTextColor(ContextCompat.getColor(context, textColorResource))


            val imageName = pet.photo
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val bitmap = loadImageAsync(context, imageName)
                    b.image.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    println("Error: $e")
                }
            }

            println(pet.birthday)

            b.age.text  = HtmlCompat.fromHtml("<b>Edad: </b>${pet.birthday}", HtmlCompat.FROM_HTML_MODE_LEGACY)
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
                            Toast.makeText(context, "EDIT ITEM ID: $id", Toast.LENGTH_SHORT).show()
                            true
                        }
                        R.id.see -> {
                            initView(id)
                            Toast.makeText(context, "VER ITEM ID: $id", Toast.LENGTH_SHORT).show()
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
                .setMessage("¿Estás seguro de que deseas eliminar este elemento?")
                .setPositiveButton("Eliminar") { _, _ ->
                    val petsQueries = PetsQueries(context)
                    petsQueries.deletePet(id.toLong())
                    Toast.makeText(context, "DELETE ITEM ID: $id", Toast.LENGTH_SHORT).show()
                }.setNegativeButton("Cancelar") { _, _ ->
                    println("INFO: NO DELETE")
                }.show()
        }

        private fun initView(id: Int) {
            println(id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        val context = parent.context
        return PetsViewHolder(PetItemBinding.inflate(LayoutInflater.from(context), parent, false), context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
