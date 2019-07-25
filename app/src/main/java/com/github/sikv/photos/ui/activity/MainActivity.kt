package com.github.sikv.photos.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.fragment.FavoritesFragment
import com.github.sikv.photos.ui.fragment.MoreFragment
import com.github.sikv.photos.ui.fragment.PhotosFragment
import com.github.sikv.photos.ui.fragment.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private val fragments = listOf(
            PhotosFragment(),
            SearchFragment(),
            FavoritesFragment(),
            MoreFragment()
    )

    private var activeFragment = fragments.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        mainBottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.photos -> {
                    changeFragment(fragments[0])
                    true
                }

                R.id.search -> {
                    changeFragment(fragments[1])
                    true
                }


                R.id.favorites -> {
                    changeFragment(fragments[2])
                    true
                }

                R.id.more -> {
                    changeFragment(fragments[3])
                    true
                }
                else -> {
                    false
                }
            }
        }

        fragments.forEachIndexed { index, fragment ->
            val transaction = supportFragmentManager.beginTransaction()
                    .add(R.id.mainNavigationContainer, fragment)

            if (index != 0) {
                transaction.hide(fragment)
            }

            transaction.commit()
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit()

        activeFragment = fragment
    }
}