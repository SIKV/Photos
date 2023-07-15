package com.github.sikv.photos.ui

import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.sikv.photos.FeatureFlagFetcher
import com.github.sikv.photos.R
import com.github.sikv.photos.common.ui.BaseFragment
import com.github.sikv.photos.databinding.ActivityMainBinding
import com.github.sikv.photos.navigation.OnDestinationChangedListener
import com.github.sikv.photos.navigation.args.SearchFragmentArguments
import com.github.sikv.photos.navigation.args.withArguments
import com.github.sikv.photos.photo.details.PhotoDetailsFragment
import com.github.sikv.photos.search.SearchFragment
import com.github.sikv.photos.ui.fragment.root.FavoritesRootFragment
import com.github.sikv.photos.ui.fragment.root.HomeRootFragment
import com.github.sikv.photos.ui.fragment.root.MoreRootFragment
import com.github.sikv.photos.ui.fragment.root.RootFragment
import com.github.sikv.photos.ui.fragment.root.SearchRootFragment
import com.github.sikv.photos.util.changeFragment
import com.github.sikv.photos.util.getActiveRootFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KClass

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var featureFlagFetcher: FeatureFlagFetcher

    private val fragmentsTag = mapOf(
        R.id.home to HomeRootFragment::class.customTag(),
        R.id.search to SearchRootFragment::class.customTag(),
        R.id.favorites to FavoritesRootFragment::class.customTag(),
        R.id.more to MoreRootFragment::class.customTag()
    )

    private var initialFragmentId = R.id.home
    private var initialDelayedFragment: Fragment? = null

    private lateinit var binding: ActivityMainBinding

    private val destinationChangedListener = object : OnDestinationChangedListener {
        override fun onDestinationChanged(fragment: Fragment?) {
            handleBottomNavigationVisibility(fragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })

        featureFlagFetcher.fetch(this) {
            when (intent.action) {
                getString(R.string._shortcut_action_search) -> {
                    getString(R.string._shortcut_search).reportShortcutUsed()

                    initialFragmentId = R.id.search
                    initialDelayedFragment = SearchFragment().withArguments(SearchFragmentArguments())
                }
            }

            if (savedInstanceState == null) {
                setupNavigation()
            }

            setNavigationListener()
            setOnDestinationChangedListener(destinationChangedListener)

            handleBottomNavigationVisibility()
        }
    }

    // FYI: Marked as public because of Lint 'Synthetic Accessor' error.
    fun handleBackPress() {
        if ((supportFragmentManager.getActiveRootFragment() as? RootFragment)?.provideNavigation()
                ?.backPressed() == false
        ) {
            if (isInitialFragmentSelected()) {
                selectInitialFragment()
            } else {
                finish()
            }
        }
    }

    override fun onDestroy() {
        setOnDestinationChangedListener(null)
        super.onDestroy()
    }

    // TODO Hide [Search] tab if ConfigProvider.getSearchSources() returns 0.
    private fun setupNavigation() {
        binding.bottomNavigationView.selectedItemId = initialFragmentId

        listOf(
            HomeRootFragment(),
            SearchRootFragment(),
            FavoritesRootFragment(),
            MoreRootFragment()
        ).forEach { fragment ->
            val tag = fragment.customTag()

            val transaction = supportFragmentManager
                .beginTransaction()
                .add(R.id.navigationContainer, fragment, tag)

            if (tag != fragmentsTag[initialFragmentId]) {
                transaction.hide(fragment)
            }

            transaction.commitNow()
        }

        val delayedFragment = initialDelayedFragment

        if (delayedFragment != null) {
            val tag = fragmentsTag[initialFragmentId]
            val fragment = supportFragmentManager.findFragmentByTag(tag) as? RootFragment

            fragment?.addDelayedFragment(delayedFragment)
        }
    }

    fun handleBottomNavigationVisibility(fragment: Fragment?) {
        // Hide bottom navigation if [Photo Details] is opened.
        val bottomNavigationVisible = fragment !is PhotoDetailsFragment
        binding.bottomNavigationView.isVisible = bottomNavigationVisible
    }

    private fun handleBottomNavigationVisibility() {
        supportFragmentManager.fragments.iterator().forEach { rootFragment ->
            if (!rootFragment.isHidden) {
                // Check the current fragment.
                handleBottomNavigationVisibility(rootFragment.childFragmentManager.fragments.lastOrNull())
            }
        }
    }

    private fun setNavigationListener() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            supportFragmentManager.changeFragment(
                hideFragmentTag = supportFragmentManager.getActiveRootFragment()?.customTag(),
                showFragmentTag = fragmentsTag[menuItem.itemId]
            )
            true
        }

        binding.bottomNavigationView.setOnItemReselectedListener { menuItem ->
            val tag = fragmentsTag[menuItem.itemId]
            val fragment = supportFragmentManager.findFragmentByTag(tag) as? RootFragment

            if (fragment?.isAdded == true) {
                (fragment.provideNavigation().backToRoot() as? BaseFragment)?.onScrollToTop()
            }
        }
    }

    private fun setOnDestinationChangedListener(destinationChangedListener: OnDestinationChangedListener?) {
        supportFragmentManager.fragments.iterator().forEach { fragment ->
            val navigation = (fragment as? RootFragment)?.provideNavigation()
            navigation?.setOnDestinationChangedListener(destinationChangedListener)
        }
    }

    private fun isInitialFragmentSelected(): Boolean {
        return binding.bottomNavigationView.selectedItemId != initialFragmentId
    }

    private fun selectInitialFragment() {
        binding.bottomNavigationView.selectedItemId = initialFragmentId
    }

    private fun KClass<out Fragment>.customTag(): String = java.simpleName

    private fun Fragment.customTag(): String = this::class.customTag()

    private fun String.reportShortcutUsed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = getSystemService(ShortcutManager::class.java) as ShortcutManager
            shortcutManager.reportShortcutUsed(this)
        }
    }
}
