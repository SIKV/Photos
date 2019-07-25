package com.github.sikv.photos.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.fragment.FavoritesFragment
import com.github.sikv.photos.ui.fragment.MoreFragment
import com.github.sikv.photos.ui.fragment.PhotosFragment
import com.github.sikv.photos.ui.fragment.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*


private const val ACTION_SEARCH = "com.github.sikv.photos.action.SEARCH"

private const val PHOTOS_FRAGMENT_INDEX = 0
private const val SEARCH_FRAGMENT_INDEX = 1
private const val FAVORITES_FRAGMENT_INDEX = 2
private const val MORE_FRAGMENT_INDEX = 3

private const val PHOTOS_ITEM_ID = R.id.photos
private const val SEARCH_ITEM_ID = R.id.search

class MainActivity : BaseActivity() {

    private val fragments = listOf(
            PhotosFragment(),
            SearchFragment(),
            FavoritesFragment(),
            MoreFragment()
    )

    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        var initialFragmentIndex = PHOTOS_FRAGMENT_INDEX
        var initialItemId = PHOTOS_ITEM_ID

        if (intent?.action.equals(ACTION_SEARCH)) {
            initialFragmentIndex = SEARCH_FRAGMENT_INDEX
            initialItemId = SEARCH_ITEM_ID
        }

        setupBottomNavigation(initialFragmentIndex, initialItemId)
    }

    private fun setupBottomNavigation(initialFragmentIndex: Int, initialItemId: Int) {
        activeFragment = fragments[initialFragmentIndex]

        mainBottomNavigation.selectedItemId = initialItemId

        fragments.forEachIndexed { index, fragment ->
            val transaction = supportFragmentManager.beginTransaction()
                    .add(R.id.mainNavigationContainer, fragment)

            if (index != initialFragmentIndex) {
                transaction.hide(fragment)
            }

            transaction.commit()
        }

        mainBottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.photos -> {
                    changeFragment(fragments[PHOTOS_FRAGMENT_INDEX])
                    true
                }

                R.id.search -> {
                    changeFragment(fragments[SEARCH_FRAGMENT_INDEX])
                    true
                }


                R.id.favorites -> {
                    changeFragment(fragments[FAVORITES_FRAGMENT_INDEX])
                    true
                }

                R.id.more -> {
                    changeFragment(fragments[MORE_FRAGMENT_INDEX])
                    true
                }
                else -> {
                    false
                }
            }
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