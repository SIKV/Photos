package com.github.sikv.photos.ui.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.fragment.*
import com.github.sikv.photos.util.DownloadPhotoState
import com.github.sikv.photos.util.SetWallpaperState
import com.github.sikv.photos.util.customTag
import com.github.sikv.photos.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        private const val ACTION_SEARCH = "com.github.sikv.photos.action.SEARCH"

        private const val KEY_FRAGMENT_TAG = "key_fragment_tag"

        private const val PHOTOS_FRAGMENT_INDEX = 0
        private const val QUEUE_FRAGMENT_INDEX = 1
        private const val SEARCH_FRAGMENT_INDEX = 2
        private const val FAVORITES_FRAGMENT_INDEX = 3
        private const val SETTINGS_FRAGMENT_INDEX = 4

        private const val PHOTOS_ITEM_ID = R.id.photos
        private const val SEARCH_ITEM_ID = R.id.search
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val fragments = listOf(
            PhotosFragment(),
            QueueFragment(),
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
        viewModel.downloadPhotoStateLiveData.observe(this, Observer { state ->
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

        viewModel.setWallpaperStateLiveData.observe(this, Observer { state ->
            when (state) {
                SetWallpaperState.SUCCESS, SetWallpaperState.FAILURE -> {
                    setWallpaperInProgressLayout.visibility = View.GONE
                }

                else -> { }
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

    private fun setNavigationItemSelectedListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.photos -> {
                    changeFragment(fragments[PHOTOS_FRAGMENT_INDEX])
                    true
                }

                R.id.queue -> {
                    changeFragment(fragments[QUEUE_FRAGMENT_INDEX])
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