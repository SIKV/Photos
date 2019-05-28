package com.github.sikv.photos.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.fragment.FavoritesFragment
import com.github.sikv.photos.ui.fragment.PhotosFragment
import com.github.sikv.photos.ui.fragment.SearchFragment
import com.github.sikv.photos.ui.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        showFragment(PhotosFragment())

        mainBottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.itemPhotos -> {
                    showFragment(PhotosFragment())
                }

                R.id.itemSearch -> {
                    showFragment(SearchFragment())
                }

                R.id.itemFavorites -> {
                    showFragment(FavoritesFragment())
                }

                R.id.itemSettings -> {
                    showFragment(SettingsFragment())
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainFragmentContainer, fragment)
        transaction.addToBackStack(null)

        transaction.commit()
    }
}