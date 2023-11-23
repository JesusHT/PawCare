package com.app.pawcare.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.R
import com.app.pawcare.databinding.CalendarNotificationItemBinding
import com.app.pawcare.models.NotificationsModel
import com.app.pawcare.models.PetsTableModel
import com.app.pawcare.slqlite.PetsQueries

class ViewCalendarNotificationsAdapter(private val list: List<NotificationsModel>) : RecyclerView.Adapter<ViewCalendarNotificationsAdapter.NotificationsViewHolder>(){
    class NotificationsViewHolder(private val b: CalendarNotificationItemBinding, private val context: Context) : RecyclerView.ViewHolder(b.root){

        data class PetInfo(val name: String, val typePet: String)
        @SuppressLint("SetTextI18n")
        fun bind(notification: NotificationsModel){
            val petData = getPetData(notification.idPet.toLong())

            b.title.text       = "${notification.title} - ${petData?.name}"
            b.description.text = notification.description

            val typeIconResource = when (Pair(petData?.typePet, notification.typeNotification)) {
                Pair("Cat", "Cita médica")     -> R.drawable.baseline_blue_hospital_24
                Pair("Cat", "Desparasitación") -> R.drawable.baseline_blue_virus_24
                Pair("Cat", "Vacunas")         -> R.drawable.baseline_blue_syringe_24
                Pair("Dog", "Cita médica")     -> R.drawable.baseline_yellow_hospital_24
                Pair("Dog", "Desparasitación") -> R.drawable.baseline_yellow_virus_24
                Pair("Dog", "Vacunas")         -> R.drawable.baseline_yellow_syringe_24
                else -> throw IllegalArgumentException("No found")
            }

            b.typeNotification.setImageResource(typeIconResource)
        }

       private fun getPetData(id: Long) : PetInfo? {
            val petsQueries = PetsQueries(context)
            var petInfo: PetInfo? = null
            val cursor = petsQueries.getPetById(id)

            if (cursor.moveToFirst()) {
                val name     = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_NAME))
                val typePet  = cursor.getString(cursor.getColumnIndexOrThrow(PetsTableModel.PetEntry.COLUMN_TYPEPET))

                petInfo = PetInfo(name, typePet)
            }

            cursor.close()
            return petInfo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val context = parent.context
        return NotificationsViewHolder(CalendarNotificationItemBinding.inflate(LayoutInflater.from(context), parent, false), context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.bind(list[position])
    }
}