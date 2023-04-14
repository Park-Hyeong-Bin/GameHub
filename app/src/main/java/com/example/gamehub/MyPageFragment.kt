package com.example.gamehub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.FragmentMypageBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var preferenceFragment: PreferenceFragment
    private lateinit var binding: FragmentMypageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        mainActivity = MainActivity()
        preferenceFragment = PreferenceFragment()

        binding.logout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this.activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.positive.setOnClickListener {
            val intent = Intent(this.activity, TagActivity::class.java)
            intent.putExtra("type", "POSITIVE")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.negative.setOnClickListener {
            val intent = Intent(this.activity, TagActivity::class.java)
            intent.putExtra("type", "NEGATIVE")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        return binding.root
    }
}
