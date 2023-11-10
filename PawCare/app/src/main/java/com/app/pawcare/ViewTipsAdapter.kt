package com.app.pawcare

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.databinding.ItemTipBinding
import com.squareup.picasso.Picasso

class ViewTipsAdapter(private val list: List<TipsModel>) : RecyclerView.Adapter<ViewTipsAdapter.TipsViewHolder>() {

    class TipsViewHolder(private val b: ItemTipBinding, private val context: Context) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(tip: TipsModel) {
            b.title.text = tip.title
            b.body.text  = tip.body

            val typeIconResource = when (tip.type) {
                "salud"     -> R.drawable.baseline_favorite_24
                "diversion" -> R.drawable.baseline_sports_baseball_24
                "alimento"  -> R.drawable.baseline_restaurant_24
                else -> throw IllegalArgumentException("No found")
            }
            b.type.setImageResource(typeIconResource)

            val typeIconResourcePet = when (tip.typePet) {
                "cat" -> R.drawable.baseline_cat_24
                "dog" -> R.drawable.baseline_dog_24
                else -> throw IllegalArgumentException("No found")
            }
            b.typePet.setImageResource(typeIconResourcePet)

            val urlImg = Config.URL_IMG + tip.img
            Picasso.get().load(urlImg).into(b.image)

            val url = tip.link
            b.readMore.setOnClickListener {
                openUrlInBrowser(url)
            }
        }

        private fun openUrlInBrowser(url: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipsViewHolder {
        val context = parent.context
        return TipsViewHolder(
            ItemTipBinding.inflate(LayoutInflater.from(context), parent, false),
            context
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: TipsViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
