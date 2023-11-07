package com.app.pawcare

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.app.pawcare.databinding.ItemTipBinding
import com.squareup.picasso.Picasso

class TipsAdapter(private val context: TipsFragment, private val arrayList: List<Tips>) : BaseAdapter(){

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val b = ItemTipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        var convertView = convertView
        convertView = b.root

        b.title.text = arrayList[position].title
        b.body.text  = arrayList[position].body

        val typeIconResource = when (arrayList[position].type) {
            "salud"       -> R.drawable.baseline_favorite_24
            "diversion"   -> R.drawable.baseline_sports_baseball_24
            "alimento"    -> R.drawable.baseline_restaurant_24

            else -> R.drawable.baseline_cake_24
        }
        b.type.setImageResource(typeIconResource)

        val typeIconResourcePet = when (arrayList[position].typePet){
            "cat" -> R.drawable.baseline_cat_24
            "dog" -> R.drawable.baseline_dog_24

            else ->  R.drawable.baseline_cake_24
        }
        b.typePet.setImageResource(typeIconResourcePet)

        val urlImg = Config.URL_IMG + arrayList[position].img
        Picasso.get().load(urlImg).into(b.image)

        val url = arrayList[position].link
        b.readMore.setOnClickListener {
            openUrlInBrowser(url)
        }

        return convertView
    }

    private fun openUrlInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}
