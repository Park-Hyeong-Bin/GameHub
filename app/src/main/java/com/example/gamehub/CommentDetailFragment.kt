package com.example.gamehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.gamehub.databinding.FragmentCommentdetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentDetailFragment(commentDto: CommentDto) : DialogFragment() {
    private lateinit var binding: FragmentCommentdetailBinding
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val db = Firebase.firestore
    private val commentdto = commentDto
    private lateinit var gameId: String
    private lateinit var favoriteDto: FavoriteDto

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCommentdetailBinding.inflate(inflater, container, false)

        gameId = arguments?.getString("gameId").toString()

        binding.comment.text = commentdto.comment
        favoriteDto = commentdto.favoriteDto!!

        binding.name.text = commentdto.name

        binding.state.isVisible = false
        binding.rating.isVisible = false

        db.collection("user")
            .document(commentdto.name)
            .collection("rating")
            .document(gameId)
            .get().addOnSuccessListener {
                if(it.get("rating") != null) {
                    binding.rating.isVisible = true
                    val value = it.get("rating").toString()
                    val rat = value.toFloat()
                    binding.rating.rating = rat
                }
            }

        db.collection("user")
            .document(commentdto.name)
            .collection("wish")
            .document(gameId)
            .get().addOnSuccessListener {
                if(it.get("state") != null) {
                    binding.state.isVisible = true
                    binding.stateimg.setImageResource(R.drawable.bookmark)
                    binding.statetxt.text = "하고싶어요"
                }
            }

        db.collection("user")
            .document(commentdto.name)
            .collection("play")
            .document(gameId)
            .get().addOnSuccessListener {
                if(it.get("state") != null) {
                    binding.state.isVisible = true
                    binding.stateimg.setImageResource(R.drawable.play)
                    binding.statetxt.text = "하는중"
                }
            }

        with(favoriteDto) {
            if (favorite.containsKey(uid)) {
                binding.favorite.setImageResource(R.drawable.favorite)
            } else {
                binding.favorite.setImageResource(R.drawable.favorite_border)
            }
            binding.favoriteNumber.text = favorite.size.toString()
        }

        binding.favorite.setOnClickListener {
            favoriteEvent()
        }

        binding.back.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id", gameId)
            val gameFragment = GameFragment()
            gameFragment.arguments = bundle
            val mainActivity = it!!.context as AppCompatActivity
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,gameFragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun favoriteEvent() {
        val map = HashMap<String, Boolean>()
        with(favoriteDto) {
            if (favorite.containsKey(uid)) {
                favorite.remove(uid)
                map.remove("state")
                binding.favorite.setImageResource(R.drawable.favorite_border)
            } else {
                favorite[uid] = true
                map["state"] = true
                binding.favorite.setImageResource(R.drawable.favorite)
            }
            binding.favoriteNumber.text = favorite.size.toString()
        }
        commentdto.favoriteDto = favoriteDto
        println(commentdto.favoriteDto)

        db.collection("comment").document(commentdto.uid).collection(gameId).document("comment")
            .get().addOnSuccessListener {
                db.collection("comment")
                    .document(commentdto.uid)
                    .collection(gameId)
                    .document("comment")
                    .set(commentdto)
            }
    }
}
