package com.example.gamehub

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamehub.databinding.HomeItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class FindViewHolder(val binding: HomeItemBinding ) : RecyclerView.ViewHolder(binding.root)



class FindAdapter(private val itemList: ArrayList<String>) :
    RecyclerView.Adapter<FindViewHolder>() {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val storage = Firebase.storage
    private val db = Firebase.firestore
    private lateinit var favoriteDto: FavoriteDto
    private lateinit var gameFav: DocumentReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindViewHolder {
        val binding = HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FindViewHolder, position: Int) {
        val item = itemList[position]
        gameFav = db.collection("favorite").document(item)
        db.collection("game").document(item).get().addOnSuccessListener {
            val imagepath = it["imagepath"].toString()
            val imageRef = storage.getReferenceFromUrl("gs://ghub-da878.appspot.com/${imagepath}")
            println(imagepath)
            val description = it["description"].toString()

            displayImageRef(imageRef, holder.binding.imageHomeGame)
            holder.binding.textHomeGame.text = description
            println(description)
        }

        gameFav.get().addOnSuccessListener {
            favoriteDto = it.toObject(FavoriteDto::class.java)!!

            with(favoriteDto) {
                if (favorite.containsKey(uid)) {
                    holder.binding.favorite.setImageResource(R.drawable.favorite)
                } else {
                    holder.binding.favorite.setImageResource(R.drawable.favorite_border)
                }
                holder.binding.favoriteNumber.text = favorite.size.toString()
            }
        }
        holder.binding.favorite.setOnClickListener {
            favoriteEvent(holder)
        }
    }

    private fun favoriteEvent(holder: FindViewHolder) {
        with(favoriteDto) {
            if (favorite.containsKey(uid)) {
                favorite.remove(uid)
                holder.binding.favorite.setImageResource(R.drawable.favorite_border)
            } else {
                favorite[uid] = true
                holder.binding.favorite.setImageResource(R.drawable.favorite)
            }
            holder.binding.favoriteNumber.text = favorite.size.toString()
        }
        gameFav.set(favoriteDto)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private fun displayImageRef(imageRef: StorageReference?, view: ImageView){
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
            println("Image load failed")
        }

    }
}
