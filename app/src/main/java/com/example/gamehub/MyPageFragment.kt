package com.example.gamehub

import android.content.Context
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

        preferenceFragment = PreferenceFragment()

        binding.logout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this.activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.positive.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("activity", "MAIN")
            bundle.putString("type", "POSITIVE")
            preferenceFragment.arguments = bundle
            mainActivity.setCurrentFragment(R.id.main_container, preferenceFragment)
        }
        binding.negative.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("activity", "MAIN")
            bundle.putString("type", "NEGATIVE")
            preferenceFragment.arguments = bundle
            mainActivity.setCurrentFragment(R.id.main_container, preferenceFragment)
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}
