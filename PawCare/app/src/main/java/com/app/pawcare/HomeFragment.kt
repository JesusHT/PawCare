package com.app.pawcare

import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.pawcare.adapters.ViewPetsAdapter
import com.app.pawcare.databinding.FragmentHomeBinding
import com.app.pawcare.interfaces.GlobalEventManager
import com.app.pawcare.interfaces.OnItemChangedListener
import com.app.pawcare.models.PetsModel
import com.app.pawcare.slqlite.NotificationsQueries
import com.app.pawcare.slqlite.PetsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment(), OnItemChangedListener {
    private lateinit var b: FragmentHomeBinding
    private lateinit var petsQueries: PetsQueries
    private lateinit var notificationsQueries: NotificationsQueries

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentHomeBinding.inflate(inflater, container, false)
        return b.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationsQueries = NotificationsQueries(requireContext())

        b.apply {
            settings.setOnClickListener      { loadProfileActivityIntent() }
            addPet.setOnClickListener        { loadAddPetActivityIntent() }
            notifications.setOnClickListener { loadNotificationsActivityIntent() }
        }

        petsQueries = PetsQueries(requireContext())
        initView()
        b.recyclerView.setOnCreateContextMenuListener(this)

        GlobalEventManager.onPetUpdateListener = { onItemChanged() }
        calculateNotifications()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNotifications(){
        val cursor = notificationsQueries.getNotificationsByDate(getDate())

        val ids = mutableListOf<String>()

        while (cursor.moveToNext()) {
            val time = cursor.getString(cursor.getColumnIndexOrThrow("time"))
            if (isCurrentTimeEqualOrBefore(time)){
                val displayName = cursor.getString(cursor.getColumnIndexOrThrow("idNotification"))
                ids.add(displayName)
            }
        }

        if (ids.size > 0){
            b.notifications.setImageResource(R.drawable.baseline_notifications_active_24)
        }else {
            b.notifications.setImageResource(R.drawable.baseline_notifications_24)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isCurrentTimeEqualOrBefore(givenTime: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = LocalTime.now()
        val givenLocalTime = LocalTime.parse(givenTime, formatter)

        return currentTime.isAfter(givenLocalTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(): String {
        val currentDate = LocalDate.now()
        val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return currentDate.format(format)
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
        loadActivityIntent(AddPetActivity::class.java)
    }
}