package com.app.pawcare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.R
import com.app.pawcare.models.PetsModel
import com.app.pawcare.databinding.PetItemBinding

class ViewPetsAdapter(private val list: List<PetsModel>) :  RecyclerView.Adapter<ViewPetsAdapter.PetsViewHolder>() {
    class PetsViewHolder(private val b: PetItemBinding, private val context: Context) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(pet: PetsModel) {
            b.Name.text = pet.name

            val typeIconResource = when (pet.raza) {
                "Cat"   -> R.drawable.photo_cat
                "Dog"   -> R.drawable.photo_dog

                else -> R.drawable.baseline_more_horiz_24
            }
            b.image.setImageResource(typeIconResource)



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