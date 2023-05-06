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

class FindFragment : Fragment() {

    private lateinit var binding: FragmentFindBinding
    private val db = Firebase.firestore
    private var itemList = ArrayList<String>()
    private lateinit var adapter: FindAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindBinding.inflate(inflater, container, false)
        binding.gameView.layoutManager = LinearLayoutManager(context)

        itemList = arrayListOf()

        adapter = FindAdapter(itemList)

        binding.gameView.adapter = adapter

        binding.searchView.isSubmitButtonEnabled = true
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null)
                    searchGames(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText == null) {
                    itemList.clear()
                    adapter.notifyDataSetChanged()
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
                    if(document.get("name").toString().contains(searchQuery)) {
                        itemList.add(document.id)
                        println(searchQuery)
                    }
                }
                adapter.notifyDataSetChanged() // 어댑터 갱신
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    companion object {
        private const val TAG = "FindFragment"
    }
}
