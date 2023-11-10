package com.app.pawcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TipsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val tips = getTips()
                    initView(tips)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun initView(tips: List<TipsModel>) {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.recyclerView)
        if (recyclerView.adapter == null) {
            val adapter = ViewTipsAdapter(tips)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private suspend fun getTips(): List<TipsModel> {
        return withContext(Dispatchers.IO) {
            val result = JsonQuery(Config.URL_TIPS).execute()
            return@withContext parseJsonToTipsList(result)
        }
    }

    private fun parseJsonToTipsList(json: String): List<TipsModel> {
        val gson = Gson()
        return gson.fromJson(json, object : TypeToken<List<TipsModel>>() {}.type)
    }
}
