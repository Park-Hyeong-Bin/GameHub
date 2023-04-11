package com.example.gamehub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamehub.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener{
            val userEmail = binding.email.text.toString()
            val password = binding.password.text.toString()
            if(userEmail != "" && password != "")
                doLogin(userEmail, password)
        }
        binding.signup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }

    private fun doLogin(userEmail: String, password: String){
        Firebase.auth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener(this){
            if(it.isSuccessful){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Log.w("LoginActivity", "signInWithEmailAndPassword", it.exception)
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}