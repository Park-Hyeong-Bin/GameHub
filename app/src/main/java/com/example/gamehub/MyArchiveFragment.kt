package com.example.gamehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gamehub.databinding.FragmentMyarchiveBinding

class MyArchiveFragment  : Fragment() {
    private lateinit var binding: FragmentMyarchiveBinding
    private lateinit var myPageFragment: MyPageFragment
    private lateinit var myRatingFragment: MyRatingFragment
    private lateinit var myWishlistFragment: MyWishlistFragment
    private lateinit var myPlaylistFragment: MyPlaylistFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyarchiveBinding.inflate(inflater, container, false)

        myRatingFragment = MyRatingFragment()
        myWishlistFragment = MyWishlistFragment()
        myPlaylistFragment = MyPlaylistFragment()

        setCurrentFragment(myRatingFragment)

        binding.btnsCategory.addOnButtonCheckedListener { _, _, isChecked ->
            if (isChecked) {
                when (binding.btnsCategory.checkedButtonId) {
                    R.id.button_rating -> setCurrentFragment(myRatingFragment)

                    R.id.button_wish -> setCurrentFragment(myWishlistFragment)

                    R.id.button_play -> setCurrentFragment(myPlaylistFragment)
                }
            }
        }

        binding.back.setOnClickListener {
            val mainActivity = context as AppCompatActivity
            myPageFragment = MyPageFragment()
            mainActivity.supportFragmentManager.beginTransaction().replace(R.id.main_container,myPageFragment).commit()
        }

        return binding.root
    }

    private fun setCurrentFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.archive_container, fragment).commit()
    }
}