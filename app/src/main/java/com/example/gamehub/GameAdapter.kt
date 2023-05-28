package com.example.gamehub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gamehub.databinding.FavoriteItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        item = itemList[position]
        gameFav = db.collection("favorite").document(item)
        gameRat = db.collection("rating").document(item)
        val imageRef = storage.getReferenceFromUrl("gs://ghub-da878.appspot.com/$item")
        val path = imageRef.child("profile.PNG")

        path.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(holder.binding.imageHomeGame.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.binding.imageHomeGame)
        }
        holder.binding.textHomeGame.text = item

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


        val bundle = Bundle()
        bundle.putString("id", item)
        val gameFragment = GameFragment()
        gameFragment.arguments = bundle

        holder.binding.card.setOnClickListener {
            val mainActivity = it!!.context as AppCompatActivity
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,gameFragment)
                .addToBackStack(null)
                .commit()
        }

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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}