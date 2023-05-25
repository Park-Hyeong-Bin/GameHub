package com.example.gamehub

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamehub.databinding.FragmentPreferenceBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PreferenceFragment : Fragment() {
    private lateinit var signupActivity: SignupActivity
    private lateinit var mainActivity: MainActivity
    private lateinit var preferenceFragment: PreferenceFragment
    private lateinit var myPageFragment: MyPageFragment
    private lateinit var binding: FragmentPreferenceBinding
    private val adapter = TagAdapter()
    private val dataSet = ArrayList<List<String>>()
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val id = auth.currentUser?.email
    private val positiveTag = db.collection("user/$id/positive_tag")
    private val negativeTag = db.collection("user/$id/negative_tag")
    private var saveContainer = ""
    private var saveType = ""
    private var count = 0
    private lateinit var tag: ArrayList<String>
    private lateinit var tagCK: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreferenceBinding.inflate(inflater, container, false)

        if (arguments != null) {
            saveContainer = arguments?.getString("activity").toString()
            saveType = arguments?.getString("type").toString()
        }

        if(saveType == "POSITIVE")
            binding.tagSelect.text = "선호 태그 선택"
        else if(saveType == "NEGATIVE")
            binding.tagSelect.text = "비선호 태그 선택"

        tag = ArrayList()
        tagCK = ArrayList()

        attach(saveContainer)

        if (saveContainer == "SIGN") {
            addData(saveType)
            binding.tagView.layoutManager =
                LinearLayoutManager(signupActivity, LinearLayoutManager.VERTICAL, false)
            binding.tagView.adapter = adapter
            binding.tagView.addItemDecoration(
                DividerItemDecoration(
                    signupActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            binding.tagView.addItemDecoration(
                DividerItemDecoration(
                    signupActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        } else if(saveContainer == "MAIN"){
            addData(saveType)
            binding.tagView.layoutManager =
                LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            binding.tagView.adapter = adapter
            binding.tagView.addItemDecoration(
                DividerItemDecoration(
                    mainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            binding.tagView.addItemDecoration(
                DividerItemDecoration(
                    mainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        adapter.setOnItemclickListner(object : TagAdapter.OnItemClickListner {
            override fun onItemClick(view: CompoundButton, position: Int) {
                if (view.isChecked) {
                    count += 1
                    tag.add(view.text.toString())
                }
                else {
                    count -= 1
                    tag.remove(view.text.toString())
                }
                tag.sort()
                binding.save.isEnabled = tag != tagCK
            }
        })

        binding.save.isEnabled = false

        binding.save.setOnClickListener {
            if(saveType == "POSITIVE") {
                for (t in tagCK)
                    positiveTag.document(t).set(hashMapOf("state" to false))

                for (t in tag)
                    positiveTag.document(t).set(hashMapOf("state" to true))
            }
            else if(saveType == "NEGATIVE") {
                for (t in tagCK)
                    negativeTag.document(t).set(hashMapOf("state" to false))

                for (t in tag)
                    negativeTag.document(t).set(hashMapOf("state" to true))
            }
            if (saveContainer == "SIGN" && saveType == "POSITIVE") {
                preferenceFragment = PreferenceFragment()
                val bundle = Bundle()
                bundle.putString("activity", "SIGN")
                bundle.putString("type", "NEGATIVE")
                preferenceFragment.arguments = bundle
                signupActivity.setCurrentFragment(R.id.signup_container, preferenceFragment)
            }
            else
                startActivity(Intent(this.activity, MainActivity::class.java))
        }
        binding.cancel.setOnClickListener {
            myPageFragment = MyPageFragment()
            mainActivity.setCurrentFragment(R.id.main_container, myPageFragment)
        }
        return binding.root
    }
    private fun addData(type: String) {
        db.collection("tag")
            .get()
            .addOnSuccessListener { result ->
                dataSet.clear()
                for (document in result) {
                    val item = document["tag"] as String
                    dataSet.add(listOf(item, "0"))
                }
                adapter.replaceList(dataSet)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        if(type == "POSITIVE") {
            db.collection("user/$id/positive_tag")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document["state"] as Boolean) {
                        val item = document.id
                        count ++
                        tag.add(item)
                        tagCK.add(item)
                        dataSet[dataSet.indexOf(listOf(item, "0"))] = listOf(item, "1")
                    }
                }
                adapter.replaceList(dataSet)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        }
        else if(type == "NEGATIVE") {
            db.collection("user/$id/negative_tag")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if(document["state"] as Boolean) {
                            val item = document.id
                            count ++
                            tag.add(item)
                            tagCK.add(item)
                            dataSet[dataSet.indexOf(listOf(item, "0"))] = listOf(item, "1")
                        }
                    }
                    adapter.replaceList(dataSet)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
    }

    private fun attach(type: String) {
        if(type == "SIGN")
            signupActivity = context as SignupActivity
        else if(type == "MAIN")
            mainActivity = context as MainActivity
    }
}

