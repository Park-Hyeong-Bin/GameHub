package com.example.gamehub

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gamehub.databinding.HomeItemBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ViewPagerAdapter(private val itemList: ArrayList<String>) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    private val storage = Firebase.storage
    private val db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding = HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PagerViewHolder(binding)
    }
    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val item = itemList[position]
        db.collection("game").document(item).get().addOnSuccessListener {
            val imagepath = it["imagepath"].toString()
            val imageRef = storage.getReferenceFromUrl("gs://ghub-da878.appspot.com")

            val path = imageRef.child(imagepath)

            val description = it["description"].toString()

            path.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(holder.binding.imageHomeGame.context)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(holder.binding.imageHomeGame)
            }
            holder.binding.textHomeGame.text = description
        }
    }

    inner class PagerViewHolder( val binding: HomeItemBinding ) : RecyclerView.ViewHolder(binding.root)
}