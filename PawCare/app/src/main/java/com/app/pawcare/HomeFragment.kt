package com.app.pawcare

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.databinding.FragmentHomeBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var b: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentHomeBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.settings.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            requireActivity().startActivity(intent)
        }

        b.addPet.setOnClickListener {
            val intent = Intent(requireActivity(), AddPetActivity::class.java)
            requireActivity().startActivity(intent)
        }

        b.notifications.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationsActivity::class.java)
            requireActivity().startActivity(intent)
        }

        val listView = b.recyclerView
        val petsModelLists = arrayListOf(
            PetsModel(1, "Chito", "Cat", "photo_cat.png", 5, "Male", "2020-03-15"),
            PetsModel(2, "Gato", "Cat", "photo_cat.png", 5, "Male", "2020-04-15"),
            PetsModel(3, "Max", "Dog", "photo_dog.png", 10, "Male", "2018-07-10"),
            PetsModel(4, "Garfield", "Dog", "photo_dog.png", 8, "Male", "2019-02-25")
        )
        initView(petsModelLists)
        listView.setOnCreateContextMenuListener(this)

    }

    private fun initView(pets: List<PetsModel>) {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.recyclerView)
        if (recyclerView.adapter == null) {
            val adapter = ViewPetsAdapter(pets)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private suspend fun getTips(): List<PetsModel> {
        return withContext(Dispatchers.IO) {
            val result = JsonQuery(Config.URL_TIPS).execute()
            return@withContext parseJsonToTipsList(result)
        }
    }

    private fun parseJsonToTipsList(json: String): List<PetsModel> {
        val gson = Gson()
        return gson.fromJson(json, object : TypeToken<List<PetsModel>>() {}.type)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        requireActivity().menuInflater.inflate(R.menu.contextual_menu, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }
}
