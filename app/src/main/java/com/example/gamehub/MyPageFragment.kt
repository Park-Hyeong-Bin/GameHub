package com.example.gamehub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private val db = Firebase.firestore
    private val id = FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var preferenceFragment: PreferenceFragment
    private lateinit var myArchiveFragment: MyArchiveFragment
    private lateinit var myCommentsFragment: MyCommentsFragment
    private lateinit var myFavoriteFragment: MyFavoriteFragment
    private lateinit var binding: FragmentMypageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        preferenceFragment = PreferenceFragment()
        myArchiveFragment = MyArchiveFragment()
        myCommentsFragment = MyCommentsFragment()
        myFavoriteFragment = MyFavoriteFragment()

        binding.logout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this.activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.textMail.text = id

        binding.rating.setOnClickListener {
            mainActivity.setCurrentFragment(R.id.main_container, myArchiveFragment)
        }

        db.collection("user")
            .document(id)
            .collection("rating")
            .get().addOnSuccessListener { documents ->
                var count = 0
                for(document in documents)
                    if(document.get("rating") != null)
                        count ++
                binding.ratingText.text = count.toString()
            }

        binding.comment.setOnClickListener {
            mainActivity.setCurrentFragment(R.id.main_container, myCommentsFragment)
        }

        db.collection("user")
            .document(id)
            .collection("comment")
            .get().addOnSuccessListener { documents ->
                var count = 0
                for(document in documents)
                    if(document.get("comment") != null)
                        count ++
                binding.commentText.text = count.toString()
            }

        binding.favorite.setOnClickListener {
            mainActivity.setCurrentFragment(R.id.main_container, myFavoriteFragment)
        }

        db.collection("user")
            .document(id)
            .collection("favorite")
            .get().addOnSuccessListener { documents ->
                var count = 0
                for(document in documents)
                    if(document.get("state") == true)
                        count ++
                binding.favoriteText.text = count.toString()
            }

        binding.likeTag.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("activity", "MAIN")
            bundle.putString("type", "POSITIVE")
            preferenceFragment.arguments = bundle
            mainActivity.setCurrentFragment(R.id.main_container, preferenceFragment)
        }

        binding.dilikeTag.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("activity", "MAIN")
            bundle.putString("type", "NEGATIVE")
            preferenceFragment.arguments = bundle
            mainActivity.setCurrentFragment(R.id.main_container, preferenceFragment)
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}
