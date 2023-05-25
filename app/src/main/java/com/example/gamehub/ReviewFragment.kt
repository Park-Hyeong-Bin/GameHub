package com.example.gamehub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.FragmentReviewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReviewFragment  : Fragment() {
    private lateinit var binding: FragmentReviewBinding

    private val db = Firebase.firestore
    private lateinit var  tagList : ArrayList<String>
    private lateinit var  gameList : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewBinding.inflate(inflater, container, false)

        tagList = arrayListOf()

        db.collection("tag").get().addOnSuccessListener { documents ->
            for (document in documents) {
                tagList.add(document.get("tag").toString())
            }
            setSpinner()
        }

        binding.search.isSubmitButtonEnabled = true
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchGames(query)
                }
                return true
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return binding.root
    }

    private fun setSpinner() {
        binding.tagSpinner.adapter = activity?.applicationContext?.let {
            ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, tagList) }

        binding.tagSpinner.setSelection(0)

        binding.tagSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gameList = arrayListOf()

                db.collection("game")
                    .whereEqualTo("tag", tagList[position]).get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            gameList.add(document.id)
                        }
                        setList()
                    }.addOnFailureListener {
                    }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun setList() {
        db.collection("user")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString())
            .collection("rating")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    if(gameList.contains(document.id) && (document.get("rating")) != null)
                        gameList.remove(document.id)
                }
                binding.game.adapter = ReviewAdapter(gameList)
            }
    }

    private fun searchGames(searchQuery: String) {
        gameList.clear()

        db.collection("game")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if(document.id.contains(searchQuery, ignoreCase = true)) {
                        gameList.add(document.id)
                        println(searchQuery)
                    }
                }
                binding.game.adapter = ReviewAdapter(gameList)
            }
    }
}