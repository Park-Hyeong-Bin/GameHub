package com.example.gamehub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.FragmentCommentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentFragment : Fragment() {
    private lateinit var binding: FragmentCommentBinding
    private val id = FirebaseAuth.getInstance().currentUser?.email.toString()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val db = Firebase.firestore
    private var first = false
    private lateinit var commentDto: CommentDto
    private lateinit var gameId: String
    private lateinit var state: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentBinding.inflate(inflater, container, false)

        gameId = arguments?.getString("id").toString()
        state = arguments?.getString("state").toString()
        if(arguments?.getBoolean("first") == true) {
            first = true
            binding.textView.text = "코멘트 작성"
        }

        db.collection("comment")
            .document(uid)
            .collection(gameId)
            .document("comment")
            .get().addOnSuccessListener {
                if(it.get("comment") != null) {
                    commentDto = it.toObject(CommentDto::class.java)!!
                    binding.comment.setText(it.get("comment").toString())
                }
                else {
                    commentDto = CommentDto()
                }
            }

        binding.save.setOnClickListener {
            commentDto.comment = binding.comment.text.toString()
            commentDto.timestamp = System.currentTimeMillis()

            if(first) {
                commentDto.uid = uid
                commentDto.name = id
                commentDto.favoriteDto = FavoriteDto()
            }
            db.collection("user")
                .document(id)
                .collection("comment")
                .document(gameId)
                .set(commentDto).addOnSuccessListener {
                    db.collection("comment")
                        .document(uid)
                        .collection(gameId)
                        .document("comment")
                        .set(commentDto)
                        .addOnSuccessListener {
                            dis()
                        }
                }
        }

        binding.cancel.setOnClickListener {
            dis()
        }
        return binding.root
    }

    private fun dis() {
        val mainActivity = context as AppCompatActivity
        when(state) {
            "game" -> {
                val bundle = Bundle()
                bundle.putString("id", gameId)
                val gameFragment = GameFragment()
                gameFragment.arguments = bundle
                mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,gameFragment).commit()
            }
            "mycomment" -> {
                val myCommentsFragment = MyCommentsFragment()
                mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,myCommentsFragment).commit()
            }
        }
    }
}
