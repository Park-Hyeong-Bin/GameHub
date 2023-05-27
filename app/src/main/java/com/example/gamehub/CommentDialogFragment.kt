package com.example.gamehub

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.gamehub.databinding.FragmentCommentdialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentDialogFragment(gameId : String) : DialogFragment() {
    private lateinit var binding: FragmentCommentdialogBinding
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val db = Firebase.firestore
    private val gamename = gameId

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCommentdialogBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setGravity(Gravity.BOTTOM)

        binding.edit.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id", gamename)
            val mainActivity = context as AppCompatActivity
            val commentFragment = CommentFragment()
            commentFragment.arguments = bundle
            mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,commentFragment).commit()
        }

        binding.delete.setOnClickListener {
            db.collection("comment")
                .document(gamename)
                .collection(uid)
                .document("comment")
                .delete()

            val bundle = Bundle()
            bundle.putString("id", gamename)
            val mainActivity = context as AppCompatActivity
            val gameFragment = GameFragment()
            gameFragment.arguments = bundle
            mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,gameFragment).commit()
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }
        return view
    }
}