package com.github.sikv.photos.ui.activity

import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.github.sikv.photos.config.feature.FeatureFlagFetcher
import com.github.sikv.photos.databinding.ActivityMainBinding
import com.github.sikv.photos.ui.fragment.BaseFragment
import com.github.sikv.photos.ui.fragment.PhotoDetailsFragment
import com.github.sikv.photos.ui.fragment.SearchFragment
import com.github.sikv.photos.ui.fragment.SearchFragmentArguments
import com.github.sikv.photos.ui.fragment.root.*
import com.github.sikv.photos.ui.navigation.OnDestinationChangedListener
import com.github.sikv.photos.ui.withArguments
import com.github.sikv.photos.util.changeFragment
import com.github.sikv.photos.util.getActiveFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KClass

@AndroidEntryPoint
class MainActivity : BaseActivity() {

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

    private val destinationChangedListener = object: OnDestinationChangedListener {
        override fun onDestinationChanged(fragment: Fragment?) {
            val bottomNavigationVisible = fragment !is PhotoDetailsFragment
            binding.bottomNavigationView.isVisible = bottomNavigationVisible
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        }
    }

    override fun onBackPressed() {
        if ((supportFragmentManager.getActiveFragment() as? RootFragment)?.provideNavigation()?.backPressed() == false) {
            if (isInitialFragmentSelected()) {
                selectInitialFragment()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        setOnDestinationChangedListener(null)

        super.onDestroy()
    }

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

    private fun setNavigationListener() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            supportFragmentManager.changeFragment(
                hideFragmentTag = supportFragmentManager.getActiveFragment()?.customTag(),
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
        supportFragmentManager.fragments.forEach { fragment ->
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
