package com.example.gamehub

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamehub.databinding.PagerItemBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>(){

    private lateinit var itemList: ArrayList<String>
    fun build(i: ArrayList<String>): HomeAdapter {
        itemList = i
        return this
    }
    class HomeViewHolder(val binding: PagerItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: String) {
            with(binding)
            {
                val db = Firebase.firestore
                val itemList2: ArrayList<String> = arrayListOf()

                binding.tag.text = item + binding.tag.text

                if(item == "전체") {
                    db.collection("game")
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                itemList2.add(document.id)
                            }
                            binding.pager.adapter = ViewPagerAdapter().build(itemList2)
                            binding.indicator.setViewPager(binding.pager)
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
                            binding.pager.adapter = ViewPagerAdapter().build(itemList2)
                            binding.indicator.setViewPager(binding.pager)
                        }.addOnFailureListener {
                        }
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder =
        HomeViewHolder(
            PagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            parent.context
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int{
        return itemList.size
    }

}