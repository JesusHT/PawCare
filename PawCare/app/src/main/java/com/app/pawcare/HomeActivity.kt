package com.app.pawcare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.pawcare.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var b : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)
        remplaceFragment(HomeFragment())

        b.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {

                R.id.home     -> remplaceFragment(HomeFragment())
                R.id.calendar -> remplaceFragment(CalendarFragment())
                R.id.tips     -> remplaceFragment(TipsFragment())
                R.id.map      -> remplaceFragment(MapsFragment())

                else -> {}
            }
            true
        }
    }

    private fun remplaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}
