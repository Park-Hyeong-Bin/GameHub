package com.example.gamehub

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamehub.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity(){
    private var checkEmail = false
    private var checkPw = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val pattern = Pattern.compile("\\w+@\\w+" + ".com")
                val matcher = pattern.matcher(charSequence)
                checkEmail = if (i == 0 && i1 != 0) {
                    binding.wrongId.text = ""
                    false
                } else if (!matcher.find()) {
                    binding.wrongId.setText(R.string.wrong_id)
                    false
                }
                else {
                    binding.wrongId.text = ""
                    true
                }
                binding.login.isEnabled = checkEmail && checkPw
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        binding.loginPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkPw = if (i == 0 && i1 != 0) {
                    binding.wrongPw.text = ""
                    false
                }
                else if (i < 7) {
                    binding.wrongPw.setTextColor(Color.RED)
                    binding.wrongPw.setText(R.string.wrong_pw)
                    false
                } else {
                    binding.wrongPw.setTextColor(Color.GRAY)
                    binding.wrongPw.setText(R.string.right_pw)
                    true
                }
                binding.login.isEnabled = checkEmail && checkPw
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        binding.login.setOnClickListener{
            val userEmail = binding.loginEmail.text.toString()
            val password = binding.loginPw.text.toString()
            if(userEmail != "" && password != "")
                doLogin(userEmail, password)
        }
        binding.signup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
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