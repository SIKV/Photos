package com.github.sikv.photos.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.RuntimeBehaviour
import com.github.sikv.photos.ui.fragment.*
import com.github.sikv.photos.util.customTag
import com.github.sikv.photos.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        private const val ACTION_SEARCH = "com.github.sikv.photos.action.SEARCH"

        private const val KEY_FRAGMENT_TAG = "fragmentTag"

        private const val PHOTOS_FRAGMENT_INDEX = 0
        private const val SEARCH_FRAGMENT_INDEX = 1
        private const val FAVORITES_FRAGMENT_INDEX = 2
        private const val SETTINGS_FRAGMENT_INDEX = 3

        private const val PHOTOS_ITEM_ID = R.id.photos
        private const val SEARCH_ITEM_ID = R.id.search

        private const val INITIAL_FRAGMENT_ID = PHOTOS_ITEM_ID
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val fragments = listOf(
            PhotosRootFragment(),
            SearchRootFragment(),
            FavoritesRootFragment(),
            SettingsRootFragment()
    )

    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        RuntimeBehaviour.init(this) {
            init(savedInstanceState)
        }
    }

    private fun init(savedInstanceState: Bundle?) {
        var initialFragmentIndex = PHOTOS_FRAGMENT_INDEX
        var initialItemId = PHOTOS_ITEM_ID

        if (intent?.action.equals(ACTION_SEARCH)) {
            initialFragmentIndex = SEARCH_FRAGMENT_INDEX
            initialItemId = SEARCH_ITEM_ID
        }

        if (savedInstanceState == null) {
            setupBottomNavigation(initialFragmentIndex, initialItemId)
        }

        setNavigationListener()

        observeGlobalMessageEvent()
    }

    override fun onBackPressed() {
        if ((activeFragment as? RootFragment)?.provideNavigation()?.backPressed() == false) {
            if (bottomNavigationView.selectedItemId != INITIAL_FRAGMENT_ID) {
                bottomNavigationView.selectedItemId = INITIAL_FRAGMENT_ID
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        supportFragmentManager.findFragmentByTag(savedInstanceState.getString(KEY_FRAGMENT_TAG))?.let { fragment ->
            activeFragment = fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        activeFragment?.let {
            outState.putString(KEY_FRAGMENT_TAG, it.customTag())
        }
    }

    private fun observeGlobalMessageEvent() {
        App.instance.globalMessageEvent.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { message ->
                Snackbar.make(rootLayout, message, Snackbar.LENGTH_SHORT)
                        .setAnchorView(bottomNavigationView)
                        .show()
            }
        })
    }

    private fun setupBottomNavigation(initialFragmentIndex: Int, initialItemId: Int) {
        activeFragment = fragments[initialFragmentIndex]

        bottomNavigationView.selectedItemId = initialItemId

        fragments.forEachIndexed { index, fragment ->
            val transaction = supportFragmentManager.beginTransaction()
                    .add(R.id.navigationContainer, fragment, fragment.customTag())

            if (index != initialFragmentIndex) {
                transaction.hide(fragment)
            }

            transaction.commit()
        }
    }

    private fun setNavigationListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            changeFragment(fragments[getFragmentIndexByItemId(menuItem.itemId)])
            true
        }

        bottomNavigationView.setOnNavigationItemReselectedListener { menuItem ->
            val fragment = supportFragmentManager.fragments[getFragmentIndexByItemId(menuItem.itemId)] as RootFragment

            if (fragment.isAdded) {
                (fragment.provideNavigation().backToRoot() as? BaseFragment)?.onScrollToTop()
            }
        }
    }

    private fun getFragmentIndexByItemId(itemId: Int): Int {
        return when (itemId) {
            R.id.photos -> PHOTOS_FRAGMENT_INDEX
            R.id.search -> SEARCH_FRAGMENT_INDEX
            R.id.favorites -> FAVORITES_FRAGMENT_INDEX
            R.id.settings -> SETTINGS_FRAGMENT_INDEX

            else -> -1
        }
    }

    private fun changeFragment(fragment: Fragment) {
        val hideFragment = findFragment(activeFragment)
        val showFragment = findFragment(fragment)

        if (hideFragment != null && showFragment != null) {
            supportFragmentManager.beginTransaction()
                    .hide(hideFragment)
                    .show(showFragment)
                    .commit()

            activeFragment = fragment
        }
    }

    private fun findFragment(fragment: Fragment?): Fragment? {
        fragment?.let {
            return supportFragmentManager.findFragmentByTag(it.customTag())
        } ?: run {
            return null
        }
    }
}