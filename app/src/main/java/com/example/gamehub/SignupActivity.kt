package com.example.gamehub

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamehub.databinding.ActivitySignupBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    lateinit var binding : ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signup.setOnClickListener {
            val userEmail = binding.username.text.toString()
            val password = binding.password.text.toString()

            binding.username.text.clear()
            binding.password.text.clear()

            doSignup(userEmail, password)
        }
    }
    private fun doSignup(userEmail: String, password: String){
        var verifiedGo = true

        if (userEmail.isEmpty() || 20 < userEmail.length) {
            Toast.makeText(this,"id를 입력해주세요", Toast.LENGTH_SHORT).show()
            verifiedGo = false
        } else if (password.isEmpty()) {
            Toast.makeText(this,"password를 입력해주세요", Toast.LENGTH_SHORT).show()
            verifiedGo = false
        } else if (password.length < 8) {
            Toast.makeText(this,"8자리 이상 입력해주세요", Toast.LENGTH_SHORT).show()
            verifiedGo = false
        }
        if(verifiedGo){
            Firebase.auth.createUserWithEmailAndPassword(userEmail,password)
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        Firebase.firestore.collection("user_pins").document(userEmail).set(hashMapOf(
                            "pin_list" to ArrayList<String>(),
                            "pined_list" to ArrayList<String>()
                        ))
                        Toast.makeText(this,"가입되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    } else Toast.makeText(this,"가입 실패하였습니다. 아마도 이미 존재하는 아이디 일 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}