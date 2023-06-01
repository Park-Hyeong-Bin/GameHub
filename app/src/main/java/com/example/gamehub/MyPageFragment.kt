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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyPageFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private val db = Firebase.firestore
    private val id = FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var preferenceFragment: PreferenceFragment
    private lateinit var myArchiveFragment: MyArchiveFragment
    private lateinit var myCommentsFragment: MyCommentsFragment
    private lateinit var myFavoriteFragment: MyFavoriteFragment
    private lateinit var binding: FragmentMypageBinding
    private lateinit var  pointList : MutableMap<String,Int>
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
        db.collection("user").document(id).get().addOnSuccessListener {
            binding.textName.text = it["name"].toString()
        }

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

        pointList = mutableMapOf()

        binding.refresh.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                refresh()
                delay(300)
                withContext(Dispatchers.Main) {
                    for (i in pointList) {
                        db.collection("user").document(id).collection("tag_point")
                            .document(i.key).set(i)
                    }
                }
            }
        }

        return binding.root
    }

    private suspend fun refresh(){
        db.collection("tag").get().addOnSuccessListener {documents ->
            for(document in documents){
                pointList[document.id.uppercase()] = 0
            }
            db.collection("user").document(id.toString()).collection("positive_tag").whereEqualTo("state", true)
                .get().addOnSuccessListener{result ->
                    for (document in result) {
                        val temp = pointList[document.id]
                        if (temp != null) {
                            pointList[document.id] = temp + 3
                        }
                    }
                }
            db.collection("user").document(id.toString()).collection("negative_tag").whereEqualTo("state", true)
                .get().addOnSuccessListener{result ->
                    for (document in result) {
                        val temp = pointList[document.id]
                        if (temp != null) {
                            pointList[document.id] = temp + -5
                        }
                    }
                }

            db.collection("user").document(id.toString()).collection("play").whereEqualTo("state",true)
                .get().addOnSuccessListener{result ->
                    for (document in result) {
                        db.collection("game").document(document.id).get().addOnSuccessListener {
                            val tagList = it["tag"] as List<*>
                            for(i in tagList){
                                val temp = pointList[i]
                                if (temp != null) {
                                    pointList[i.toString()] = temp + 5
                                }
                            }
                        }

                    }
                }
            db.collection("user").document(id.toString()).collection("wish").whereEqualTo("state",true)
                .get().addOnSuccessListener{result ->
                    for (document in result) {
                        db.collection("game").document(document.id).get().addOnSuccessListener {
                            val tagList = it["tag"] as List<*>
                            for(i in tagList){
                                val temp = pointList[i]
                                if (temp != null) {
                                    pointList[i.toString()] = temp + 3
                                }
                            }
                        }

                    }
                }
            db.collection("user").document(id.toString()).collection("rating")
                .get().addOnSuccessListener{result ->
                    for (document in result) {
                        if(document.exists()){
                            val rating =  document["rating"]
                            db.collection("game").document(document.id).get().addOnSuccessListener {
                                val tagList = it["tag"] as List<*>
                                for(i in tagList){
                                    val temp = pointList[i]
                                    if (temp != null) {
                                        when(rating){
                                            1.0 -> pointList[i.toString()] = temp + -5
                                            2.0 -> pointList[i.toString()] = temp + -3
                                            4.0 -> pointList[i.toString()] = temp + 3
                                            5.0 -> pointList[i.toString()] = temp + 5
                                        }
                                    }
                                }
                            }


                        }

                    }
                }
            db.collection("user").document(id.toString()).collection("favorite").whereEqualTo("state",true)
                .get().addOnSuccessListener{result ->
                    for (document in result) {
                        db.collection("game").document(document.id).get().addOnSuccessListener {
                            val tagList = it["tag"] as List<*>
                            for(i in tagList){
                                val temp = pointList[i]
                                if (temp != null) {
                                    pointList[i.toString()] = temp + 3
                                }
                            }
                        }

                    }
                }

        }.await()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

}
