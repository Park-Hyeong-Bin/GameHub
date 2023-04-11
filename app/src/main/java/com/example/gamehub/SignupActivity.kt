package com.example.gamehub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity(){
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signFragment: SignFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signFragment = SignFragment()

        setCurrentFragment(R.id.signup_container, signFragment)
    }
    fun setCurrentFragment(id: Int, fragment : Fragment)
            = supportFragmentManager.beginTransaction().apply {
        replace(id,fragment)
        commit()
    }
}