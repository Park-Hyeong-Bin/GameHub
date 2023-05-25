package com.example.gamehub

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gamehub.databinding.ReviewItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ReviewAdapter(private val dataset: ArrayList<String>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>(){

    private val storage = Firebase.storage
    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private lateinit var gameRating: DocumentReference
    private lateinit var ratingDto: RatingDto

    class ReviewViewHolder(val binding: ReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder =
        ReviewViewHolder(
            ReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = dataset[position]

        gameRating = db.collection("rating").document(item)

        val imageRef = storage.getReferenceFromUrl("gs://ghub-da878.appspot.com/$item")
        val path = imageRef.child("profile.PNG")

        path.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(holder.binding.imageHomeGame.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)

                .into(holder.binding.imageHomeGame)
        }
        holder.binding.textHomeGame.text = item

        gameRating.get().addOnSuccessListener {
            ratingDto = it.toObject(RatingDto::class.java)!!
        }

        holder.binding.ratingbarStyle.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
            ratingDto.rating[uid] = rating

            val map = HashMap<String, Float>()

            if(rating < 0.5)
                ratingDto.rating.remove(uid)
            else
                map["rating"] = rating

            db.collection("user").document(FirebaseAuth.getInstance().currentUser?.email.toString())
                .collection("rating")
                .document(item)
                .set(map)

            gameRating.set(ratingDto)
        }
    }

    override fun getItemCount(): Int{
        return dataset.size
    }
}