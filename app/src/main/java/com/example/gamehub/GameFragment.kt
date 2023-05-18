package com.example.gamehub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gamehub.databinding.FragmentGameBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private val storage = Firebase.storage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)

        val gameId = arguments?.getString("id").toString()

        binding.textViewName.text = gameId

        val imageRef = storage.getReferenceFromUrl("gs://ghub-da878.appspot.com/$gameId")
        val textfile = imageRef.child("description.txt")
        val localfile = File.createTempFile("description.txt","txt")

        textfile.getFile(localfile).addOnSuccessListener {
            val description = localfile.readText()
            binding.textViewDes.text = description
        }

        val itemlist: ArrayList<String> = arrayListOf("profile.PNG", "play_1.PNG", "play_2.PNG", "play_3.PNG")

        binding.pager.adapter = GamePagerAdapter().build(itemlist, gameId)
        binding.indicator.setViewPager(binding.pager)

        return binding.root
    }
}