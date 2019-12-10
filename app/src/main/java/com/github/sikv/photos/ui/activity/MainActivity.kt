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
        private const val MORE_FRAGMENT_INDEX = 4

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

        if (savedInstanceState == null) {
            setupBottomNavigation(initialFragmentIndex, initialItemId)
        }

        setNavigationItemSelectedListener()

        setWallpaperSetListeners()

        observe()
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
                    mainSetWallpaperInProgressLayout.visibility = View.VISIBLE
                    mainSetWallpaperDownloadingLayout.visibility = View.VISIBLE
                    mainSetWallpaperStatusLayout.visibility = View.GONE
                    mainSetWallpaperButton.visibility = View.GONE
                    mainSetWallpaperCancelButton.visibility = View.VISIBLE
                }

                DownloadPhotoState.PHOTO_READY -> {
                    mainSetWallpaperInProgressLayout.visibility = View.VISIBLE
                    mainSetWallpaperDownloadingLayout.visibility = View.GONE
                    mainSetWallpaperStatusLayout.visibility = View.VISIBLE
                    mainSetWallpaperButton.visibility = View.VISIBLE
                    mainSetWallpaperCancelButton.visibility = View.VISIBLE

                    mainSetWallpaperStatusImage.setImageResource(R.drawable.ic_check_green_24dp)
                    mainSetWallpaperStatusText.setText(R.string.photo_ready)
                }

                DownloadPhotoState.CANCELED -> {
                    mainSetWallpaperInProgressLayout.visibility = View.GONE
                }

                DownloadPhotoState.ERROR_DOWNLOADING_PHOTO -> {
                    mainSetWallpaperInProgressLayout.visibility = View.VISIBLE
                    mainSetWallpaperDownloadingLayout.visibility = View.GONE
                    mainSetWallpaperStatusLayout.visibility = View.VISIBLE
                    mainSetWallpaperButton.visibility = View.GONE
                    mainSetWallpaperCancelButton.visibility = View.VISIBLE

                    mainSetWallpaperStatusImage.setImageResource(R.drawable.ic_close_red_24dp)
                    mainSetWallpaperStatusText.setText(R.string.error_downloading_photo)
                }

                else -> { }
            }
        })

        viewModel.setWallpaperStateLiveData.observe(this, Observer { state ->
            when (state) {
                SetWallpaperState.SUCCESS -> {
                    mainSetWallpaperInProgressLayout.visibility = View.GONE
                }

                SetWallpaperState.FAILURE -> {
                    mainSetWallpaperInProgressLayout.visibility = View.GONE
                }

                else -> { }
            }
        })
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

    private fun setWallpaperSetListeners() {
        mainSetWallpaperButton.setOnClickListener {
            viewModel.setWallpaper()
        }

        mainSetWallpaperCancelButton.setOnClickListener {
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