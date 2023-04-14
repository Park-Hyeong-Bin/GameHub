package com.example.gamehub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.ActivityTagBinding

class TagActivity : AppCompatActivity(){
    private lateinit var binding: ActivityTagBinding
    private lateinit var preferenceFragment: PreferenceFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("type")
        val bundle = Bundle()
        bundle.putString("activity", "TAG")
        bundle.putString("type", type)
        preferenceFragment = PreferenceFragment()
        preferenceFragment.arguments = bundle

        setCurrentFragment(R.id.tag_container, preferenceFragment)
    }
    private fun setCurrentFragment(id: Int, fragment : Fragment)
            = supportFragmentManager.beginTransaction().apply {
        replace(id,fragment)
        commit()
    }
}