package com.example.gamehub

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamehub.databinding.HomeItemBinding
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage



class HomeViewHolder(val binding: HomeItemBinding ) : RecyclerView.ViewHolder(binding.root)



class HomeAdapter(private var itemList: ArrayList<String>)
    : RecyclerView.Adapter<HomeViewHolder>(){

    private val storage = Firebase.storage
    private val db = Firebase.firestore




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {

        val binding = HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return HomeViewHolder(binding)

    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val item = itemList[position]
        val gameDB = db.collection("game").document(item).get().addOnSuccessListener {
            val imagepath = it["imagepath"].toString()
            val imageRef = storage.getReferenceFromUrl("gs://ghub-da878.appspot.com/${imagepath}")
            println(imagepath)
            val description = it["description"].toString()

            displayImageRef(imageRef, holder.binding.imageHomeGame)
            holder.binding.textHomeGame.text = description
            println(description)
        }




    }

    override fun getItemCount(): Int{
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: ArrayList<String>) {
        itemList = newList
        notifyDataSetChanged()
    }


    private fun displayImageRef(imageRef: StorageReference?, view: ImageView){
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener(){
            println("Image load failed")
        }

    }


}