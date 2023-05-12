package com.example.gamehub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gamehub.databinding.HomeItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ViewPagerAdapter(private var itemList: ArrayList<String>) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    private lateinit var binding: HomeItemBinding
    private val storage = Firebase.storage
    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private lateinit var gameFav: DocumentReference
    private lateinit var favoriteDto: FavoriteDto

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        binding = HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PagerViewHolder(binding)
    }
    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: ArrayList<String>) {
        itemList = newList
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val item = itemList[position]


        gameFav = db.collection("favorite").document(item)

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

        val bundle = Bundle()
        bundle.putString("id", item)

        holder.binding.imageHomeGame.setOnClickListener {
            val mainActivity = it!!.context as AppCompatActivity
            val gameFragment = GameFragment()
            gameFragment.arguments = bundle
            mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,gameFragment).commit()
        }
        holder.binding.textHomeGame.setOnClickListener {
            val mainActivity = it!!.context as AppCompatActivity
            val gameFragment = GameFragment()
            gameFragment.arguments = bundle
            mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,gameFragment).commit()
        }
    }




    private fun favoriteEvent(holder: PagerViewHolder) {
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




    inner class PagerViewHolder( val binding: HomeItemBinding ) : RecyclerView.ViewHolder(binding.root)
}



