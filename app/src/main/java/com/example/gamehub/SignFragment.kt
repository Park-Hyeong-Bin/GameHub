package com.example.gamehub

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.FragmentSignBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.regex.Pattern

class SignFragment : Fragment() {

    private lateinit var binding: FragmentSignBinding
    private lateinit var preferenceFragment: PreferenceFragment
    private val db: FirebaseFirestore = Firebase.firestore
    private val usersCollectionRef = db.collection("user")
    private lateinit var mActivity: SignupActivity
    private var checkEmail = false
    private var checkPw = false
    private var checkPwConfirm = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignBinding.inflate(inflater, container, false)

        preferenceFragment = PreferenceFragment()
        mActivity = this.activity as SignupActivity

        val birth = binding.birth
        val minDate = Calendar.getInstance()
        minDate.add(Calendar.YEAR, -100)
        birth.minDate = minDate.timeInMillis
        val maxDate = Calendar.getInstance()
        birth.maxDate = maxDate.timeInMillis

        binding.signEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val pattern = Pattern.compile("\\w+@\\w+" + ".com")
                val matcher = pattern.matcher(charSequence)
                checkEmail = if(i == 0 && i1 != 0) {
                    binding.wrongSignEmail.text = ""
                    false
                }
                else if (!matcher.find()) {
                    binding.wrongSignEmail.setText(R.string.wrong_id)
                    false
                }
                else {
                    binding.wrongSignEmail.text = ""
                    true
                }
                binding.signup.isEnabled = checkEmail && checkPw && checkPwConfirm
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        binding.signPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkPw = if(i == 0 && i1 != 0) {
                    binding.wrongSignPw.text = ""
                    false
                } else if (i < 7) {
                    binding.wrongSignPw.setTextColor(Color.RED)
                    binding.wrongSignPw.setText(R.string.wrong_pw)
                    false
                } else {
                    binding.wrongSignPw.setTextColor(Color.GRAY)
                    binding.wrongSignPw.setText(R.string.right_pw)
                    true
                }
                binding.signup.isEnabled = checkEmail && checkPw && checkPwConfirm
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        binding.signPwConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkPwConfirm = if(i == 0 && i1 != 0) {
                    binding.wrongSignPwConfirm.text = ""
                    false
                } else if (binding.signPw.text.toString() != charSequence.toString()) {
                    binding.wrongSignPwConfirm.setTextColor(Color.RED)
                    binding.wrongSignPwConfirm.setText(R.string.ckpw_1)
                    false
                } else {
                    binding.wrongSignPwConfirm.setTextColor(Color.GRAY)
                    binding.wrongSignPwConfirm.setText(R.string.ckpw_2)
                    true
                }
                binding.signup.isEnabled = checkEmail && checkPw && checkPwConfirm
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        binding.signup.setOnClickListener { doSignup() }
        return binding.root
    }

    private fun doSignup() {
        val email = binding.signEmail.text.toString()
        val password = binding.signPw.text.toString()
        val birth = binding.birth
        val year = birth.year
        val month = birth.month + 1
        val day = birth.dayOfMonth
        val gender = binding.gender.selectedItem.toString()

        val userMap = hashMapOf(
            "name" to binding.signName.text,
            "year" to year,
            "month" to month,
            "day" to day,
            "gender" to gender
        )
        CoroutineScope(Dispatchers.Main).launch {
            if (Firebase.auth.currentUser == null) {
                Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            }
            usersCollectionRef.document(email).set(userMap)
            doLogin(email, password)
        }
    }
    private fun doLogin(userEmail: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(mActivity) {
                if (it.isSuccessful) {
                    val bundle = Bundle()
                    bundle.putString("activity", "SIGN")
                    bundle.putString("type", "POSITIVE")
                    preferenceFragment = PreferenceFragment()
                    preferenceFragment.arguments = bundle

                    mActivity.setCurrentFragment(R.id.signup_container, preferenceFragment)
                } else {
                    Log.w("SignupActivity", "signInWithEmailAndPassword", it.exception)
                    Toast.makeText(mActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}