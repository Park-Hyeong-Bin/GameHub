package com.example.gamehub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.FragmentPreferenceBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PreferenceFragment : Fragment() {
    private lateinit var binding: FragmentPreferenceBinding
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionRef = db.collection("user")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentPreferenceBinding.inflate(inflater, container, false)


        val listner = CompoundButton.OnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked) {
                when(buttonView.id){
                    R.id.tag_FPS -> isChecked
                    R.id.tag_RPG -> isChecked
                }
            binding.next.isEnabled = true}
            else binding.next.isEnabled = false
        }
        binding.next.isEnabled = false
        binding.tagRPG.setOnCheckedChangeListener(listner)
        binding.tagFPS.setOnCheckedChangeListener(listner)

        binding.next.setOnClickListener {
            startActivity(Intent(this.activity, MainActivity::class.java))
        }
        binding.skip.setOnClickListener {
            startActivity(Intent(this.activity, MainActivity::class.java))
        }
        return binding.root
    }


    /*private fun checkedBoxes(){
        var oneChecked: Boolean = false

        if(rpg == true){
            oneChecked = true
        }

        return oneChecked
    }

    private fun disableBtn(){
        return
    }*/



}