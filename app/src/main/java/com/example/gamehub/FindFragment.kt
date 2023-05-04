package com.example.gamehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.FragmentFindBinding

class FindFragment : Fragment() {

    private lateinit var binding: FragmentFindBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindBinding.inflate(inflater, container, false)

        return binding.root
    }


}