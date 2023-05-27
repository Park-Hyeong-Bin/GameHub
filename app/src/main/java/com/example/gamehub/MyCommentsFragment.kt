package com.example.gamehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.FragmentMycommentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyCommentsFragment  : Fragment() {
    private lateinit var binding: FragmentMycommentsBinding

    private val db = Firebase.firestore
    private val id = FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var gameList : ArrayList<String>
    private lateinit var myPageFragment: MyPageFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMycommentsBinding.inflate(inflater, container, false)

        gameList = arrayListOf()

        db.collection("user")
            .document(id)
            .collection("comment")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    if(document.get("comment") != null) {
                        gameList.add(document.id)
                    }
                }
                binding.game.adapter = MyCommentsAdapter(gameList, childFragmentManager)
            }

        binding.back.setOnClickListener {
            val mainActivity = context as AppCompatActivity
            myPageFragment = MyPageFragment()
            mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,myPageFragment).commit()
        }

        return binding.root
    }
}