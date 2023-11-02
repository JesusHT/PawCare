package com.app.pawcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TipsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            GlobalScope.launch(Dispatchers.Main) {
                getTips()
            }
        }
    }

    private suspend fun getTips(){
        val result = JsonQuery(Config.URL_TIPS).execute()

        val tipsList = parseJsonToTipsList(result)

        val listView: ListView = requireView().findViewById(R.id.listView)
        val adapter = TipsAdapter(this@TipsFragment, tipsList)
        listView.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (NetworkUtils.isNetworkAvailable(requireContext())) {
            inflater.inflate(R.layout.fragment_tips, container, false)
        } else {
            inflater.inflate(R.layout.fragment_wifi, container, false)
        }
    }

    private fun parseJsonToTipsList(json: String): List<Tips> {
        val gson = Gson()
        return gson.fromJson(json, object : TypeToken<List<Tips>>() {}.type)
    }
}