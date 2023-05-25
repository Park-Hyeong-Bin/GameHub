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
import com.example.gamehub.databinding.MyratingItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyRatingAdapter(private val dataset: ArrayList<String>) : RecyclerView.Adapter<MyRatingAdapter.MyRatingViewHolder>(){

    private val storage = Firebase.storage
    private val db = Firebase.firestore
    private val id = FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var gameRating: DocumentReference
    private lateinit var ratingDto: RatingDto

    class MyRatingViewHolder(val binding: MyratingItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRatingViewHolder =
        MyRatingViewHolder(
            MyratingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyRatingViewHolder, position: Int) {
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

            with(ratingDto) {
                var sum = 0F
                for(rat in rating) {
                    sum += rat.value
                }
                holder.binding.avgrating.text = "평균 ★ " + sum / rating.size
            }
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

        db.collection("user")
            .document(id)
            .collection("rating")
            .document(item)
            .get().addOnSuccessListener {
                holder.binding.myrating.text = "평가함 ★ " + it.get("rating")
            }

        db.collection("game")
            .document(item)
            .get().addOnSuccessListener {
                holder.binding.tag.text = "장르 : " + it.get("tag")
            }
    }

    override fun getItemCount(): Int{
        return dataset.size
    }
}