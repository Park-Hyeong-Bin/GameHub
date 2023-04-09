package com.example.gamehub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var findFragment: FindFragment
    private lateinit var myPageFragment: MyPageFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeFragment = HomeFragment()
        findFragment = FindFragment()
        myPageFragment = MyPageFragment()

        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        initBottomNavigation()
    }
    private fun initBottomNavigation() {
        setCurrentFragment(homeFragment)
        binding.bottomNavi.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> setCurrentFragment(homeFragment)
                R.id.action_find -> setCurrentFragment(findFragment)
                R.id.action_mypage -> setCurrentFragment(myPageFragment)
            }
            return@setOnItemSelectedListener true
        }
    }
    private fun setCurrentFragment(fragment : Fragment)
    = supportFragmentManager.beginTransaction().apply {
        replace(R.id.fl_container,fragment)
        commit()
    }
}