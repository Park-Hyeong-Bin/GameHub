package com.example.gamehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamehub.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase




class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var db = Firebase.firestore
    private var adapter : HomeAdapter? = null
    private lateinit var  itemList : ArrayList<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.RecyclerViewHome.layoutManager = LinearLayoutManager(context)



        itemList = arrayListOf()

        db = FirebaseFirestore.getInstance()

        db.collection("game").get().addOnSuccessListener {
            if(!it.isEmpty){

                for(data in it){


                   itemList.add(data.id)
                }
                binding.RecyclerViewHome.adapter = HomeAdapter(itemList)
            }


        }.addOnFailureListener{
           //Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }


        return binding.root
    }
}