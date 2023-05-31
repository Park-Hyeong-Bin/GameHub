package com.example.gamehub

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamehub.databinding.FragmentFindBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class FindFragment : Fragment() {

    private lateinit var binding: FragmentFindBinding
    private val db = Firebase.firestore
    private var itemList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindBinding.inflate(inflater, container, false)
        binding.gameView.layoutManager = LinearLayoutManager(context)

        itemList = arrayListOf()

        binding.gameView.adapter = GameAdapter(itemList)

        binding.searchView.isSubmitButtonEnabled = true
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if(query.substring(0 until 1) == "#")
                        searchTag(query)
                    else
                        searchGames(query)
                }
                return true
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText == null) {
                    itemList.clear()
                    binding.gameView.adapter = GameAdapter(itemList)
                }
                return true
            }
        })
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchGames(searchQuery: String) {
        itemList.clear()

        db.collection("game")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if(document.id.contains(searchQuery, ignoreCase = true)) {
                        itemList.add(document.id)
                    }
                }
                binding.gameView.adapter = GameAdapter(itemList)

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchTag(searchQuery: String) {
        itemList.clear()

        val sQ = searchQuery.substring(1).uppercase(Locale.ROOT)

        db.collection("game")
            .whereArrayContains("tag", sQ)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    itemList.add(document.id)
                }
                binding.gameView.adapter = GameAdapter(itemList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    companion object {
        private const val TAG = "FindFragment"
    }
}
