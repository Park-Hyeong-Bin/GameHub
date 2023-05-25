package com.example.gamehub

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gamehub.databinding.FavoriteItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class GameViewHolder(val binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root)

class GameAdapter(private val itemList: ArrayList<String>) :
    RecyclerView.Adapter<GameViewHolder>() {

    private val storage = Firebase.storage
    private val db = Firebase.firestore
    private val id = FirebaseAuth.getInstance().currentUser?.email.toString()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private lateinit var favoriteDto: FavoriteDto
    private lateinit var gameFav: DocumentReference
    private lateinit var ratingDto: RatingDto
    private lateinit var gameRat: DocumentReference
    private lateinit var item: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        item = itemList[position]
        gameFav = db.collection("favorite").document(item)
        gameRat = db.collection("rating").document(item)

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

        gameRat.get().addOnSuccessListener {
            ratingDto = it.toObject(RatingDto::class.java)!!

            with(ratingDto) {
                var sum = 0F
                for(rat in rating) {
                    sum += rat.value
                }

                if(rating.isEmpty())
                    holder.binding.imageView.setImageResource(R.drawable.star2)

                else {
                    holder.binding.rating.text = (sum / rating.size).toString()
                    holder.binding.ratingCount.text = "(${rating.size})"
                }
            }
        }

        holder.binding.favorite.setOnClickListener {
            favoriteEvent(holder)
        }

        val listener : View.OnClickListener = View.OnClickListener { v ->
            val bundle = Bundle()
            bundle.putString("id", item)
            val mainActivity = v!!.context as AppCompatActivity
            val gameFragment = GameFragment()
            gameFragment.arguments = bundle
            mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,gameFragment).commit()
        }

        holder.binding.card.setOnClickListener(listener)
        holder.binding.favorite.setOnClickListener {
            favoriteEvent(holder)
        }
    }

    private fun favoriteEvent(holder: GameViewHolder) {
        val map = HashMap<String, Boolean>()
        with(favoriteDto) {
            if (favorite.containsKey(uid)) {
                favorite.remove(uid)
                map.remove("state")
                holder.binding.favorite.setImageResource(R.drawable.favorite_border)
            } else {
                favorite[uid] = true
                map["state"] = true
                holder.binding.favorite.setImageResource(R.drawable.favorite)
            }
            holder.binding.favoriteNumber.text = favorite.size.toString()
        }
        gameFav.set(favoriteDto)
        gameFav.set(favoriteDto)
        db.collection("user")
            .document(id)
            .collection("favorite")
            .document(item)
            .set(map)
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