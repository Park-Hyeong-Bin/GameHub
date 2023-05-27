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
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val db = Firebase.firestore

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentBinding.inflate(inflater, container, false)

        val gameId = arguments?.getString("id").toString()

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

            db.collection("comment")
                .document(gameId)
                .collection(uid)
                .document("comment")
                .set(map)
                .addOnSuccessListener {
                    val bundle = Bundle()
                    bundle.putString("id", gameId)
                    val mainActivity = context as AppCompatActivity
                    val gameFragment = GameFragment()
                    gameFragment.arguments = bundle
                    mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,gameFragment).commit()
                }
        }

        binding.cancel.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id", gameId)
            val mainActivity = context as AppCompatActivity
            val gameFragment = GameFragment()
            gameFragment.arguments = bundle
            mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,gameFragment).commit()
        }
        return binding.root
    }
}
