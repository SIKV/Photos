package com.github.sikv.photos.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.fragment.FavoritesFragment
import com.github.sikv.photos.ui.fragment.PhotosFragment
import com.github.sikv.photos.ui.fragment.SearchFragment
import com.github.sikv.photos.ui.fragment.SettingsFragment
import com.github.sikv.photos.util.customTag
import kotlinx.android.synthetic.main.activity_main.*


private const val ACTION_SEARCH = "com.github.sikv.photos.action.SEARCH"

private const val PHOTOS_FRAGMENT_INDEX = 0
private const val SEARCH_FRAGMENT_INDEX = 1
private const val FAVORITES_FRAGMENT_INDEX = 2
private const val SETTINGS_FRAGMENT_INDEX = 3

private const val PHOTOS_ITEM_ID = R.id.photos
private const val SEARCH_ITEM_ID = R.id.search

private const val FRAGMENT_TAG = "fragment_tag"


class MainActivity : BaseActivity() {

    private val fragments = listOf(
            PhotosFragment(),
            SearchFragment(),
            FavoritesFragment(),
            SettingsFragment()
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

        if (savedInstanceState == null) {
            setupBottomNavigation(initialFragmentIndex, initialItemId)
        }

        setNavigationItemSelectedListener()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        supportFragmentManager.findFragmentByTag(savedInstanceState?.getString(FRAGMENT_TAG))?.let { fragment ->
            activeFragment = fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(FRAGMENT_TAG, activeFragment.customTag())
    }

    private fun setupBottomNavigation(initialFragmentIndex: Int, initialItemId: Int) {
        activeFragment = fragments[initialFragmentIndex]

        mainBottomNavigation.selectedItemId = initialItemId

        fragments.forEachIndexed { index, fragment ->
            val transaction = supportFragmentManager.beginTransaction()
                    .add(R.id.mainNavigationContainer, fragment, fragment.customTag())

            if (index != initialFragmentIndex) {
                transaction.hide(fragment)
            }

            transaction.commit()
        }
    }

    private fun setNavigationItemSelectedListener() {
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

                R.id.settings -> {
                    changeFragment(fragments[SETTINGS_FRAGMENT_INDEX])
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
                .hide(findFragment(activeFragment))
                .show(findFragment(fragment))
                .commit()

        activeFragment = fragment

    }

    private fun findFragment(fragment: Fragment): Fragment {
        return supportFragmentManager.findFragmentByTag(fragment.customTag())!!
    }
}