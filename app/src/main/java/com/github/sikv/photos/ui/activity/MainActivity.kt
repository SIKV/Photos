package com.github.sikv.photos.ui.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.DownloadPhotoState
import com.github.sikv.photos.enumeration.SetWallpaperState
import com.github.sikv.photos.ui.fragment.*
import com.github.sikv.photos.util.customTag
import com.github.sikv.photos.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        private const val ACTION_SEARCH = "com.github.sikv.photos.action.SEARCH"

        private const val KEY_FRAGMENT_TAG = "key_fragment_tag"

        private const val PHOTOS_FRAGMENT_INDEX = 0
        private const val SEARCH_FRAGMENT_INDEX = 1
        private const val FAVORITES_FRAGMENT_INDEX = 2
        private const val SETTINGS_FRAGMENT_INDEX = 3

        private const val PHOTOS_ITEM_ID = R.id.photos
        private const val SEARCH_ITEM_ID = R.id.search
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val fragments = listOf(
            PhotosFragment(),
            SearchDashboardFragment(),
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

        setNavigationListener()

        setWallpaperSetListeners()

        observe()
    }

    override fun onBackPressed() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible) {
                val childFragment = fragment.childFragmentManager

                if (childFragment.backStackEntryCount > 0) {
                    childFragment.popBackStack()
                    return
                }
            }
        }

        super.onBackPressed()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        supportFragmentManager.findFragmentByTag(savedInstanceState?.getString(KEY_FRAGMENT_TAG))?.let { fragment ->
            activeFragment = fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_FRAGMENT_TAG, activeFragment.customTag())
    }

    private fun observe() {
        viewModel.downloadPhotoStateChangedLiveData.observe(this, Observer { state ->
            when (state) {
                DownloadPhotoState.DOWNLOADING_PHOTO -> {
                    setWallpaperInProgressLayout.visibility = View.VISIBLE
                    setWallpaperDownloadingLayout.visibility = View.VISIBLE
                    setWallpaperStatusLayout.visibility = View.GONE
                    setWallpaperButton.visibility = View.GONE
                    setWallpaperCancelButton.visibility = View.VISIBLE

                    setWallpaperInProgressText.setText(R.string.downloading_photo)
                }

                DownloadPhotoState.PHOTO_READY -> {
                    setWallpaperInProgressLayout.visibility = View.VISIBLE
                    setWallpaperDownloadingLayout.visibility = View.GONE
                    setWallpaperStatusLayout.visibility = View.VISIBLE
                    setWallpaperButton.visibility = View.VISIBLE
                    setWallpaperCancelButton.visibility = View.VISIBLE

                    setWallpaperStatusImage.setImageResource(R.drawable.ic_check_green_24dp)
                    setWallpaperStatusText.setText(R.string.photo_ready)
                }

                DownloadPhotoState.ERROR_DOWNLOADING_PHOTO -> {
                    setWallpaperInProgressLayout.visibility = View.VISIBLE
                    setWallpaperDownloadingLayout.visibility = View.GONE
                    setWallpaperStatusLayout.visibility = View.VISIBLE
                    setWallpaperButton.visibility = View.GONE
                    setWallpaperCancelButton.visibility = View.VISIBLE

                    setWallpaperStatusImage.setImageResource(R.drawable.ic_close_red_24dp)
                    setWallpaperStatusText.setText(R.string.error_downloading_photo)
                }

                DownloadPhotoState.CANCELING -> {
                    setWallpaperInProgressLayout.visibility = View.VISIBLE
                    setWallpaperDownloadingLayout.visibility = View.VISIBLE
                    setWallpaperStatusLayout.visibility = View.GONE
                    setWallpaperButton.visibility = View.GONE
                    setWallpaperCancelButton.visibility = View.GONE

                    setWallpaperInProgressText.setText(R.string.canceling)
                }

                DownloadPhotoState.CANCELED -> {
                    setWallpaperInProgressLayout.visibility = View.GONE
                }

                else -> { }
            }
        })

        viewModel.setWallpaperStateChangedEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { state ->
                when (state) {
                    SetWallpaperState.SUCCESS, SetWallpaperState.FAILURE -> {
                        setWallpaperInProgressLayout.visibility = View.GONE

                        if (state == SetWallpaperState.FAILURE) {
                            postMessage(R.string.error_setting_wallpaper)
                        }
                    }
                }
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
            val fragment = supportFragmentManager.fragments[getFragmentIndexByItemId(menuItem.itemId)] as BaseFragment

            if (!fragment.isAdded) {
                return@setOnNavigationItemReselectedListener
            }

            val childFragment = fragment.childFragmentManager

            if (childFragment.backStackEntryCount > 0) {
                for (i in childFragment.backStackEntryCount downTo 1) {
                    childFragment.popBackStack()
                }
            } else {
                fragment.onScrollToTop()
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

    private fun setWallpaperSetListeners() {
        setWallpaperButton.setOnClickListener {
            viewModel.setWallpaper()
        }

        setWallpaperCancelButton.setOnClickListener {
            viewModel.cancelSetWallpaper()
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