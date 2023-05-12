package com.example.gamehub

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gamehub.databinding.FragmentGameBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)

        val gameId = arguments?.getString("id").toString()

        binding.textViewName.text = gameId
        db.collection("game").document(gameId).get().addOnSuccessListener {
            val imagepath = it["imagepath"].toString()
            val imageRef = storage.getReferenceFromUrl("gs://ghub-da878.appspot.com")
            val path = imageRef.child(imagepath)
            val description = it["description"].toString()

            path.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(binding.imageView.context)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)

                    .into(binding.imageView)
            }
            binding.textViewDes.text = description
        }
        return binding.root
    }




}