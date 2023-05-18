package com.example.gamehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gamehub.databinding.GamePagerItemBinding
import com.example.gamehub.databinding.HomeItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class GamePagerAdapter : RecyclerView.Adapter<GamePagerAdapter.PagerViewHolder>() {
    private lateinit var itemList: ArrayList<String>
    private lateinit var gameID: String
    private lateinit var binding: GamePagerItemBinding
    private val storage = Firebase.storage

    fun build(i : ArrayList<String>, g : String): GamePagerAdapter {
        itemList = i
        gameID = g
        return this
    }

    class PagerViewHolder( val binding: GamePagerItemBinding ) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        binding = GamePagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PagerViewHolder(binding)
    }
    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val item = itemList[position]

        val imageRef = storage.getReferenceFromUrl("gs://ghub-da878.appspot.com/$gameID")
        val path = imageRef.child(item)

        path.downloadUrl.addOnSuccessListener { uri ->
            println("success")
            Glide.with(holder.binding.image.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(holder.binding.image)
        }
    }
}



