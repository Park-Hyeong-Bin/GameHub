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

        db.collection("comment")
            .document(gameId)
            .collection(uid)
            .document("comment")
            .get().addOnSuccessListener {
                if(it.get("comment") != null)
                    binding.comment.setText(it.get("comment").toString())
            }

        binding.save.setOnClickListener {
            val map = HashMap<String, String>()

            map["comment"] = binding.comment.text.toString()

            db.collection("user")
                .document(id)
                .collection("comment")
                .document(gameId)
                .set(map)
                .addOnSuccessListener {
                    db.collection("comment")
                        .document(gameId)
                        .collection(uid)
                        .document("comment")
                        .set(map)
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
