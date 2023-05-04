package com.example.gamehub

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamehub.databinding.PagerItemBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeViewHolder(val binding: PagerItemBinding ) : RecyclerView.ViewHolder(binding.root)

class HomeAdapter(private var itemList: ArrayList<String>)
    : RecyclerView.Adapter<HomeViewHolder>(){

    private lateinit var binding: PagerItemBinding
    private val db = Firebase.firestore
    private lateinit var itemList2: ArrayList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        binding = PagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return HomeViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = itemList[position]

        itemList2 = arrayListOf()

        binding.tag.text = item + binding.tag.text

        if(item == "전체") {
            db.collection("game")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        itemList2.add(document.id)
                    }
                    binding.pager.adapter = ViewPagerAdapter(itemList2)
                }.addOnFailureListener {
                }
        }
        else {
            db.collection("game")
                .whereEqualTo("tag", item).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        itemList2.add(document.id)
                    }
                    binding.pager.adapter = ViewPagerAdapter(itemList2)
                }.addOnFailureListener {
                }
        }
    }

    override fun getItemCount(): Int{
        return itemList.size
    }
}