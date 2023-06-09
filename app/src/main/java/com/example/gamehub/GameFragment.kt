package com.example.gamehub

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.gamehub.databinding.FragmentGameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private val id = FirebaseAuth.getInstance().currentUser?.email.toString()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val db = Firebase.firestore
    private lateinit var ratingDto : RatingDto
    private lateinit var commentDto: CommentDto
    private lateinit var commentlist: ArrayList<CommentDto>
    private var wish = false
    private var play = false

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)

        val gameId = arguments?.getString("id").toString()

        binding.textViewName.text = gameId

        db.collection("game").document(gameId).get().addOnSuccessListener {
            binding .textViewDes.text = it["description"].toString()
        }

        val itemlist: ArrayList<String> = arrayListOf("profile.PNG", "play_1.PNG", "play_2.PNG", "play_3.PNG")

        binding.pager.adapter = GamePagerAdapter().build(itemlist, gameId)
        binding.indicator.setViewPager(binding.pager)

        db.collection("rating")
            .document(gameId)
            .get().addOnSuccessListener {
                ratingDto = it.toObject(RatingDto::class.java)!!

                with(ratingDto) {
                    var sum = 0F
                    for(rat in rating) {
                        if(rat.key == uid)
                            binding.ratingbarStyle.rating = rat.value
                        sum += rat.value
                    }
                    binding.avgrating.text = "평균 ★ " + sum / rating.size
                }
        }

        db.collection("user")
            .document(id)
            .collection("wish")
            .document(gameId)
            .get().addOnSuccessListener {
                if(it.get("state") == true) {
                    binding.buttonWish.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bookmark, 0, 0)
                    wish = true
                }
            }

        db.collection("user")
            .document(id)
            .collection("play")
            .document(gameId)
            .get().addOnSuccessListener {
                if(it.get("state") == true) {
                    binding.buttonPlay.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bookmark, 0, 0)
                    play = true
                }
            }

        db.collection("comment")
            .document(uid)
            .collection(gameId)
            .document("comment")
            .get().addOnSuccessListener {
                if(it.get("comment") != null) {
                    binding.mycomment.text = "내 코멘트\n" + it.get("comment").toString()
                }
                else {
                    binding.mycomment.isVisible = false
                    binding.editComment.isVisible = false
                    binding.editComment.isClickable = false
                }
            }

        db.collection("user")
            .document(id)
            .collection("positive_tag")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.get("state") == true) {
                        db.collection("game")
                            .whereArrayContains("tag", document.id)
                            .get().addOnSuccessListener { documents2 ->
                                for (document2 in documents2) {
                                    if (document2.id == gameId) {
                                        print(document2.id)
                                        if(binding.textreson.text == "")
                                            binding.textreson.text = "선호하는 태그 #" + document.id
                                        else
                                            binding.textreson.text = binding.textreson.text.toString() + ", " + document.id
                                    }
                                }
                            }
                    }
                }
                if(binding.textreson.text == "") {
                    binding.textreson.visibility = GONE
                    binding.line1.visibility = GONE
                    binding.reson.visibility = GONE
                }
            }

        db.collectionGroup(gameId).get().addOnSuccessListener { documents ->
            commentlist = arrayListOf()
            for (document in documents) {
                commentDto = document.toObject(CommentDto::class.java)
                commentlist.add(commentDto)
            }
            binding.comment.adapter = CommentListAdapter(gameId, commentlist)
        }

        binding.editComment.setOnClickListener {
            val dialog = CommentDialogFragment(gameId)
            val bundle = Bundle()
            bundle.putString("state", "game")
            dialog.arguments = bundle
            dialog.show(childFragmentManager, "comment")
        }

        binding.buttonWish.setOnClickListener {
            val map = HashMap<String, Boolean>()
            map["state"] = true

            if(wish) {
                db.collection("user")
                    .document(id)
                    .collection("wish")
                    .document(gameId)
                    .delete()

                binding.buttonWish.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add, 0, 0)
            }
            else {
                db.collection("user")
                    .document(id)
                    .collection("play")
                    .document(gameId)
                    .delete()

                db.collection("user")
                    .document(id)
                    .collection("wish")
                    .document(gameId)
                    .set(map)

                binding.buttonWish.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bookmark, 0, 0)
                binding.buttonPlay.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.play, 0, 0)
            }
        }

        binding.buttonComment.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id", gameId)
            if(!binding.mycomment.isVisible)
                bundle.putBoolean("first", true)
            bundle.putString("state", "game")
            val mainActivity = context as AppCompatActivity
            val commentFragment = CommentFragment()
            commentFragment.arguments = bundle
            mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,commentFragment).commit()
        }

        binding.buttonPlay.setOnClickListener {
            val map = HashMap<String, Boolean>()
            map["state"] = true

            if(play) {
                db.collection("user")
                    .document(id)
                    .collection("play")
                    .document(gameId)
                    .delete()

                binding.buttonPlay.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.play, 0, 0)
            }
            else {
                db.collection("user")
                    .document(id)
                    .collection("wish")
                    .document(gameId)
                    .delete()

                db.collection("user")
                    .document(id)
                    .collection("play")
                    .document(gameId)
                    .set(map)

                binding.buttonWish.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add, 0, 0)
                binding.buttonPlay.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bookmark, 0, 0)
            }
        }

        binding.ratingbarStyle.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
            ratingDto.rating[uid] = rating

            val map = HashMap<String, Float>()

            if(rating < 0.5)
                ratingDto.rating.remove(uid)
            else
                map["rating"] = rating

            db.collection("user").document(FirebaseAuth.getInstance().currentUser?.email.toString())
                .collection("rating")
                .document(gameId)
                .set(map)

            db.collection("rating")
                .document(gameId)
                .set(ratingDto)
        }


        binding.buttonURL.setOnClickListener {
            db.collection("game").document(gameId).get().addOnSuccessListener {
                val url = it["URL"].toString()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                val mainActivity = context as AppCompatActivity
                mainActivity.startActivity(intent)
            }
        }


        return binding.root
    }


}


