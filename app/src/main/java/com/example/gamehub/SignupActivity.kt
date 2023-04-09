package com.example.gamehub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamehub.databinding.ActivitySignupBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignupActivity : AppCompatActivity(){
    private val db : FirebaseFirestore = Firebase.firestore
    //private val usersCollectionRef = db.collection("users")
    val database = Firebase.database
    private lateinit var binding: ActivitySignupBinding
    var flag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signup.setOnClickListener{
            val passwordtext = binding.password.text.toString()
            val passwordtextConfirm = binding.passwordconfirm.text.toString()
            if (passwordtext == passwordtextConfirm){
                if(flag == 0){
                    val email = binding.username.text.toString()
                    CoroutineScope(Dispatchers.Main).launch {
                        if(Firebase.auth.currentUser == null){
                            Firebase.auth.createUserWithEmailAndPassword(email, passwordtext).await()
                        }
                        //usersCollectionRef.document(email).set(userMap)
                        doLogin(email, passwordtext)
                    }
                }
            }
        }
    }

    private fun doLogin(userEmail: String, password: String){
        Firebase.auth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener(this) {
            if(it.isSuccessful){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Log.w("SignupActivity", "signInWithEmailAndPassword", it.exception)
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}