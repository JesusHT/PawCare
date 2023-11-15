package com.app.pawcare.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.R
import com.app.pawcare.models.PetsModel
import com.app.pawcare.databinding.PetItemBinding
import java.io.File
import java.io.FileInputStream

class ViewPetsAdapter(private val list: List<PetsModel>) :  RecyclerView.Adapter<ViewPetsAdapter.PetsViewHolder>() {
    class PetsViewHolder(private val b: PetItemBinding, private val context: Context) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(pet: PetsModel) {
            b.Name.text = pet.name

            val textColorResource = when (pet.typePet) {
                "Cat" -> R.color.sky_blue
                "Dog" -> R.color.yellow
                else -> throw IllegalArgumentException("No found")
            }
            b.Name.setTextColor(ContextCompat.getColor(context, textColorResource))

            val imageName = pet.photo
            getImage(context, imageName, b.image)

            b.raza.text = pet.raza
            b.peso.text = pet.peso.toString()
            b.sex.text = pet.sex
            b.btnMenu.setOnClickListener { v ->
                showContextMenu(v, pet.idPet)
            }

            b.image.setOnClickListener {
                initView(pet.idPet)
            }
        }

        private fun showContextMenu(view: View, id : Int) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.menuInflater.inflate(R.menu.contextual_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        Toast.makeText(context, "ELIMINAR ITEM ID: $id", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.edit -> {
                        Toast.makeText(context, "EDITAR ITEM ID: $id", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.see -> {
                        Toast.makeText(context, "VER ITEM ID: $id", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        private fun initView(id: Int){
            println(id)
        }

        fun getImage(context: Context, nombreImagen: String, imageView: ImageView) {
            val directorioDocumentos = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val rutaCompleta = File(directorioDocumentos, nombreImagen)

            if (rutaCompleta.exists()) {
                loadImage(imageView).execute(rutaCompleta)
            } else {
                println("Error: No encontrada")
            }
        }

        private class loadImage(val imageView: ImageView) : AsyncTask<File, Void, Bitmap>() {
            override fun doInBackground(vararg params: File): Bitmap? {
                return try {
                    val fileInputStream = FileInputStream(params[0])
                    BitmapFactory.decodeStream(fileInputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            override fun onPostExecute(result: Bitmap?) {
                if (result != null) {
                    imageView.setImageBitmap(result)
                } else {
                    println("Error: La carga no puedo ser completada")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        val context = parent.context
        return PetsViewHolder(
            PetItemBinding.inflate(LayoutInflater.from(context), parent, false),
            context
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        holder.bind(list[position])
    }
}