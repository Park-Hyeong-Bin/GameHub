package com.example.gamehub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gamehub.databinding.CommentItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyCommentsAdapter(private val dataset: ArrayList<String>, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<MyCommentsAdapter.MyCommentViewHolder>(){

    private val storage = Firebase.storage
    private val db = Firebase.firestore
    private val id = FirebaseAuth.getInstance().currentUser?.email.toString()

    class MyCommentViewHolder(val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCommentViewHolder =
        MyCommentViewHolder(
            CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyCommentViewHolder, position: Int) {
        val item = dataset[position]

        val imageRef = storage.getReferenceFromUrl("gs://ghub-da878.appspot.com/$item")
        val path = imageRef.child("profile.PNG")

        path.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(holder.binding.imageHomeGame.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)

                .into(holder.binding.imageHomeGame)
        }
        holder.binding.textHomeGame.text = item

        db.collection("user")
            .document(id)
            .collection("wish")
            .document(item)
            .get().addOnSuccessListener {
                if(it.get("state") != null) {
                    holder.binding.stateimg.setImageResource(R.drawable.bookmark)
                    holder.binding.statetxt.text = "하고싶어요"
                }
            }

        db.collection("user")
            .document(id)
            .collection("play")
            .document(item)
            .get().addOnSuccessListener {
                if(it.get("state") != null) {
                    holder.binding.stateimg.setImageResource(R.drawable.play)
                    holder.binding.statetxt.text = "하는중"
                }
            }

        db.collection("user")
            .document(id)
            .collection("comment")
            .document(item)
            .get().addOnSuccessListener {
                holder.binding.comment.text = it.get("comment").toString()
            }

        holder.binding.editComment.setOnClickListener {
            val dialog = CommentDialogFragment(item)
            val bundle = Bundle()
            bundle.putString("state", "mycomment")
            dialog.arguments = bundle
            dialog.show(fragmentManager, "comment")
        }
    }

    override fun getItemCount(): Int{
        return dataset.size
    }
}
