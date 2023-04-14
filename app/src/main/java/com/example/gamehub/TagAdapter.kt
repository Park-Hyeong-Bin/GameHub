package com.example.gamehub

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.gamehub.databinding.TagItemBinding

class TagAdapter : RecyclerView.Adapter<TagAdapter.ViewHolder>(){
    private var data = ArrayList<List<String>>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = TagItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }
    override fun getItemCount(): Int {
        return data.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun replaceList(newList: ArrayList<List<String>>) {
        data = newList
        notifyDataSetChanged()
    }

    interface OnItemClickListner{
        fun onItemClick(view: CompoundButton, position: Int)
    }

    fun setOnItemclickListner(listner: OnItemClickListner){
        listener = listner
    }

    private lateinit var listener: OnItemClickListner

    inner class ViewHolder(private val binding: TagItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: List<String>) {
            binding.gameTag.text = data[0]
            binding.gameTag.isChecked = data[1] == "1"
            binding.gameTag.setOnClickListener {
                val pos = adapterPosition
                if(pos != RecyclerView.NO_POSITION){
                    listener.onItemClick(binding.gameTag,pos)
                }
            }
        }

    }
}
