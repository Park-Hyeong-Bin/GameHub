package com.example.gamehub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
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
import java.util.Calendar

class SignupActivity : AppCompatActivity(){
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionRef = db.collection("user")
    private lateinit var binding: ActivitySignupBinding
    private var flag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //생년월일 minmax 설정
        val birth = binding.birth
        val minDate = Calendar.getInstance()
        minDate.add(Calendar.YEAR, -100)
        birth.minDate = minDate.timeInMillis
        val maxDate = Calendar.getInstance()
        birth.maxDate = maxDate.timeInMillis

        binding.signup.setOnClickListener{
            flag = 0
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()

            val passwordtext = binding.password.text.toString()
            val passwordtextConfirm = binding.passwordconfirm.text.toString()
            if(name.isEmpty()){
                Toast.makeText(this,"이름을 입력해주세요",Toast.LENGTH_SHORT).show()
                flag = 1
            }
            if (email.isEmpty() || !email.contains("@")) {
                Toast.makeText(this,"올바른 email을 입력해주세요",Toast.LENGTH_SHORT).show()
                flag = 1
            } else if (passwordtext.isEmpty()) {
                Toast.makeText(this,"password를 입력해주세요",Toast.LENGTH_SHORT).show()
                flag = 1
            } else if (passwordtextConfirm.isEmpty()) {
                Toast.makeText(this,"password확인을 입력해주세요",Toast.LENGTH_SHORT).show()
                flag = 1
            } else if (passwordtext != passwordtextConfirm) {
                Toast.makeText(this,"비밀번호를 똑같이 입력해 주세요",Toast.LENGTH_SHORT).show()
                flag = 1
            }

            if(flag == 0){
                val year = birth.year
                val month = birth.month + 1
                val day = birth.dayOfMonth
                val gender = binding.gender.selectedItem.toString()

                val userMap = hashMapOf(
                    "name" to name,
                    "year" to year,
                    "month" to month,
                    "day" to day,
                    "gender" to gender
                )

                CoroutineScope(Dispatchers.Main).launch {
                    if(Firebase.auth.currentUser == null){
                        Firebase.auth.createUserWithEmailAndPassword(email, passwordtext).await()
                    }
                    usersCollectionRef.document(email).set(userMap)
                    doLogin(email, passwordtext)
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