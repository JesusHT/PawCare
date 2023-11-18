package com.app.pawcare

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.adapters.ViewPetsAdapter
import com.app.pawcare.databinding.FragmentHomeBinding
import com.app.pawcare.interfaces.OnItemChangedListener
import com.app.pawcare.models.PetsModel
import com.app.pawcare.slqlite.PetsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment(), OnItemChangedListener {
    private lateinit var b: FragmentHomeBinding
    private lateinit var petsQueries: PetsQueries

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentHomeBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.apply {
            settings.setOnClickListener      { loadProfileActivityIntent() }
            addPet.setOnClickListener        { loadAddPetActivityIntent() }
            notifications.setOnClickListener { loadNotificationsActivityIntent() }
        }

        petsQueries = PetsQueries(requireContext())
        initView()
        b.recyclerView.setOnCreateContextMenuListener(this)
    }

    private fun initView() {
        lifecycleScope.launch(Dispatchers.Main) {
            val pets = getPetsFromDatabase()
            updateRecyclerView(pets)
        }
    }

    private suspend fun getPetsFromDatabase(): List<PetsModel> {
        return withContext(Dispatchers.IO) {
            val petsCursor = petsQueries.getAllPets()
            return@withContext parsePetsCursorToList(petsCursor)
        }
    }

    private fun parsePetsCursorToList(cursor: Cursor): List<PetsModel> {
        val petsList = mutableListOf<PetsModel>()

        while (cursor.moveToNext()) {
            val idPet    = cursor.getInt(cursor.getColumnIndexOrThrow("idPet"))
            val name     = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val raza     = cursor.getString(cursor.getColumnIndexOrThrow("raza"))
            val photo    = cursor.getString(cursor.getColumnIndexOrThrow("photo"))
            val peso     = cursor.getInt(cursor.getColumnIndexOrThrow("peso"))
            val sex      = cursor.getString(cursor.getColumnIndexOrThrow("sex"))
            val birthday = cursor.getString(cursor.getColumnIndexOrThrow("birthday"))
            val typePet  = cursor.getString(cursor.getColumnIndexOrThrow("typePet"))

            val pet = PetsModel(1, idPet, name, raza, photo, peso, sex, birthday, typePet)
            petsList.add(pet)
        }

        cursor.close()
        return petsList
    }

    private fun updateRecyclerView(pets: List<PetsModel>) {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.recyclerView)
        if (recyclerView.adapter == null) {
            val adapter = ViewPetsAdapter(pets, this)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onItemChanged() {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.recyclerView)
        recyclerView.adapter = null
        initView()
    }
    /*
        METHODS TO LOAD INTENT
    */
    private fun loadActivityIntent(destinationActivity: Class<*>) {
        val intent = Intent(requireActivity(), destinationActivity)
        requireActivity().startActivity(intent)
    }

    private fun loadProfileActivityIntent() {
        loadActivityIntent(ProfileActivity::class.java)
    }

    private fun loadNotificationsActivityIntent() {
        loadActivityIntent(NotificationsActivity::class.java)
    }

    private fun loadAddPetActivityIntent() {
        val intent = Intent(requireActivity(), AddPetActivity::class.java)
        requireActivity().startActivity(intent)
        requireActivity().finish()
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
