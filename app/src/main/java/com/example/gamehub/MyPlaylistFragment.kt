package com.example.gamehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.FragmentMyplaylistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyPlaylistFragment  : Fragment() {
    private lateinit var binding: FragmentMyplaylistBinding

    private val db = Firebase.firestore
    private val id = FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var  gameList : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyplaylistBinding.inflate(inflater, container, false)

        gameList = arrayListOf()

        db.collection("user")
            .document(id)
            .collection("play")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    if(document.get("state") != null) {
                        gameList.add(document.id)
                    }
                }
                binding.game.adapter = GameAdapter(gameList)
            }

        return binding.root
    }
}