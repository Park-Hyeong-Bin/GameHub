package com.example.gamehub

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamehub.databinding.PagerItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.LinkedList

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
                val tagList: ArrayList<String> = arrayListOf()
                val gameMap: MutableMap<String, Long> = mutableMapOf()
                val email = FirebaseAuth.getInstance().currentUser?.email.toString()
                binding.tag.text = " 게임"
                binding.tag.text = item +  binding.tag.text

                binding.pager.adapter =HomePagerAdapter().build(itemList2)

                when (item) {
                    "전체" -> {
                        db.collection("game")
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    itemList2.add(document.id)

                                }
                                binding.pager.adapter = HomePagerAdapter().build(itemList2)
                                binding.indicator.setViewPager(binding.pager)


                            }.addOnFailureListener {
                            }
                    }
                    "추천" -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            db.collection("user").document(email)
                                .collection("tag_point").orderBy("value", Query.Direction.DESCENDING).get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents){
                                        println("tag ${document.id}")
                                        //tagList.add(document.id)
                                        db.collection("game").whereArrayContains("tag",document.id).get().addOnSuccessListener { result ->
                                            for(i in result){
                                                if(gameMap.containsKey(i.id)){
                                                    val temp = gameMap[i.id]
                                                    if (temp != null) {
                                                        gameMap[i.id] = temp + document["value"] as Long
                                                    }
                                                }
                                                else{
                                                    gameMap[i.id] = document["value"] as Long
                                                }

                                            }
                                        }
                                    }
                                }.await()
                            delay(300)
                            withContext(Dispatchers.Main){
                                val entries = LinkedList(gameMap.entries)
                                entries.sortByDescending { it.value }
                                val resultMap = LinkedHashMap<String,Long>()
                                for((index,entry) in entries.withIndex()){
                                    if(index < 10){
                                        resultMap[entry.key] = entry.value
                                        println("result $entry")
                                        itemList2.add(entry.key)
                                    }
                                }
                                binding.pager.adapter = HomePagerAdapter().build(itemList2)
                                binding.indicator.setViewPager(binding.pager)
                            }

                        }



                    }
                    else -> {
                        db.collection("game")
                            .whereArrayContains("tag", item).get()
                            .addOnSuccessListener {documents ->
                                for (document in documents)  {
                                    itemList2.add(document.id)
                                }

                                binding.pager.adapter = HomePagerAdapter().build(itemList2)
                                binding.indicator.setViewPager(binding.pager)

                            }.addOnFailureListener {
                                println("HOME ADAPTER FAILED")
                            }
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