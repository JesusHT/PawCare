package com.app.pawcare

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.pawcare.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {
    private lateinit var b: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentCalendarBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.addReminder.setOnClickListener {
            val intent = Intent(requireActivity(), RemindersActivity::class.java)
            requireActivity().startActivity(intent)
        }
    }
}