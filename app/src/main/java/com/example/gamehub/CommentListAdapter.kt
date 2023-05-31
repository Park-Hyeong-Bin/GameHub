package com.example.gamehub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.gamehub.databinding.CommentlistItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentListViewHolder(val binding: CommentlistItemBinding) : RecyclerView.ViewHolder(binding.root)

class CommentListAdapter(private val gameId: String, private val itemList: ArrayList<CommentDto>) :
    RecyclerView.Adapter<CommentListViewHolder>() {

    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private lateinit var favoriteDto: FavoriteDto
    private lateinit var item: CommentDto

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentListViewHolder {
        val binding = CommentlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentListViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CommentListViewHolder, position: Int) {
        item = itemList[position]

        holder.binding.comment.text = item.comment
        favoriteDto = item.favoriteDto!!

        holder.binding.name.text = item.name

        holder.binding.state.isVisible = false
        holder.binding.rating.isVisible = false

        db.collection("user")
            .document(item.name)
            .collection("rating")
            .document(gameId)
            .get().addOnSuccessListener {
                if(it.get("rating") != null) {
                    holder.binding.rating.isVisible = true
                    val value = it.get("rating").toString()
                    val rat = value.toFloat()
                    holder.binding.rating.rating = rat
                }
            }

        db.collection("user")
            .document(item.name)
            .collection("wish")
            .document(gameId)
            .get().addOnSuccessListener {
                if(it.get("state") != null) {
                    holder.binding.state.isVisible = true
                    holder.binding.stateimg.setImageResource(R.drawable.bookmark)
                    holder.binding.statetxt.text = "하고싶어요"
                }
            }

        db.collection("user")
            .document(item.name)
            .collection("play")
            .document(gameId)
            .get().addOnSuccessListener {
                if(it.get("state") != null) {
                    holder.binding.state.isVisible = true
                    holder.binding.stateimg.setImageResource(R.drawable.play)
                    holder.binding.statetxt.text = "하는중"
                }
            }

        with(favoriteDto) {
            if (favorite.containsKey(uid)) {
                holder.binding.favorite.setImageResource(R.drawable.favorite)
            } else {
                holder.binding.favorite.setImageResource(R.drawable.favorite_border)
            }
            holder.binding.favoriteNumber.text = favorite.size.toString()
        }

        holder.binding.favorite.setOnClickListener {
            favoriteEvent(holder)
        }

        val bundle = Bundle()
        val commentDetailFragment = CommentDetailFragment(item)
        bundle.putString("gameId", gameId)
        commentDetailFragment.arguments = bundle
        holder.binding.card.setOnClickListener {
            val mainActivity = it!!.context as AppCompatActivity
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,commentDetailFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun favoriteEvent(holder: CommentListViewHolder) {
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
        item.favoriteDto = favoriteDto
        println(item.favoriteDto)

        db.collection("comment").document(item.uid).collection(gameId).document("comment")
            .get().addOnSuccessListener {
                db.collection("comment")
                    .document(item.uid)
                    .collection(gameId)
                    .document("comment")
                    .set(item)
            }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}