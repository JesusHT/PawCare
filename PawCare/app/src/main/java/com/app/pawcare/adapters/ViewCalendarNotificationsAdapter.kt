package com.app.pawcare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.databinding.CalendarNotificationItemBinding
import com.app.pawcare.models.NotificationsModel

class ViewCalendarNotificationsAdapter(private val list: List<NotificationsModel>) : RecyclerView.Adapter<ViewCalendarNotificationsAdapter.NotificationsViewHolder>(){
    class NotificationsViewHolder(private val b: CalendarNotificationItemBinding, private val context: Context) : RecyclerView.ViewHolder(b.root){
        fun bind(notification: NotificationsModel){
            b.title.text = notification.title

            println(notification.date)
            println(notification.time)
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