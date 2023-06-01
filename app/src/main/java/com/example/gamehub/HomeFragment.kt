package com.example.gamehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamehub.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val db = Firebase.firestore
    private val id = FirebaseAuth.getInstance().currentUser?.email
    private lateinit var  itemList : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.RecyclerViewHome.layoutManager = LinearLayoutManager(context)

        itemList = arrayListOf()
        db.collection("user")
            .document(id.toString())
            .collection("positive_tag")
            .whereEqualTo("state", true)
            .get().addOnSuccessListener { documents ->
                itemList.add("전체")
                itemList.add("추천")
                if(!documents.isEmpty) {
                    for (document in documents) {
                        itemList.add(document.id)
                    }
                }
                binding.RecyclerViewHome.adapter = HomeAdapter().build(itemList)
            }.addOnFailureListener {
            }
        return binding.root
    }
}