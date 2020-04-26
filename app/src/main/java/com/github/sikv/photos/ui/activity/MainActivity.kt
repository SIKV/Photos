package com.github.sikv.photos.ui.activity

import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.RuntimeBehaviour
import com.github.sikv.photos.model.FragmentInfo
import com.github.sikv.photos.ui.fragment.*
import com.github.sikv.photos.util.customTag
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        private const val ACTION_SHORTCUT_SEARCH = "com.github.sikv.photos.action.SHORTCUT_SEARCH"

        private const val SHORTCUT_SEARCH = "search"
    }

    private val fragments = listOf(
            FragmentInfo(
                    PhotosRootFragment(),
                    R.id.photos
            ),
            FragmentInfo(
                    SearchRootFragment(),
                    R.id.search
            ),
            FragmentInfo(
                    FavoritesRootFragment(),
                    R.id.favorites
            ),
            FragmentInfo(
                    SettingsRootFragment(),
                    R.id.settings
            )
    )

    private val photosFragmentIndex = 0
    private val searchFragmentIndex = 1

    private var initialFragmentInfo = fragments[photosFragmentIndex]

    private var shortcutManager: ShortcutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            shortcutManager = getSystemService(ShortcutManager::class.java) as ShortcutManager
        }

        RuntimeBehaviour.init(this) {
            when (intent.action) {
                ACTION_SHORTCUT_SEARCH -> {
                    reportShortcutUsed(SHORTCUT_SEARCH)

                    initialFragmentInfo = fragments[searchFragmentIndex]

                    (fragments[searchFragmentIndex].fragment as? RootFragment)
                            ?.addFragmentDelayed(SearchFragment.newInstance())
                }
            }

            if (savedInstanceState == null) {
                setupBottomNavigation()
            }

            setNavigationListener()
            observeGlobalMessageEvent()
        }
    }

    override fun onBackPressed() {
        if ((getActiveFragment() as? RootFragment)?.provideNavigation()?.backPressed() == false) {
            if (isInitialFragmentSelected()) {
                selectInitialFragment()
            } else {
                super.onBackPressed()
            }
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

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = initialFragmentInfo.itemId

        fragments.forEach { fragmentInfo ->
            val transaction = supportFragmentManager.beginTransaction()
                    .add(R.id.navigationContainer, fragmentInfo.fragment, fragmentInfo.fragment.customTag())

            if (fragmentInfo != initialFragmentInfo) {
                transaction.hide(fragmentInfo.fragment)
            }

            transaction.commit()
        }
    }

    private fun setNavigationListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            changeFragment(menuItem.itemId)
            true
        }

        bottomNavigationView.setOnNavigationItemReselectedListener { menuItem ->
            val fragmentTag = getFragmentTagByItemId(menuItem.itemId)
            val fragment = findFragment(fragmentTag) as RootFragment

            if (fragment.isAdded) {
                (fragment.provideNavigation().backToRoot() as? BaseFragment)?.onScrollToTop()
            }
        }
    }

    private fun getFragmentTagByItemId(itemId: Int): String? {
        return fragments.find { it.itemId == itemId }?.fragment?.customTag()
    }

    private fun changeFragment(@IdRes itemId: Int) {
        val hideFragment = findFragment(getActiveFragment()?.customTag())
        val showFragment = findFragment(getFragmentTagByItemId(itemId))

        val tr = supportFragmentManager.beginTransaction()

        if (hideFragment != null) {
           tr.hide(hideFragment)
        }
        if (showFragment != null) {
            tr.show(showFragment)
        }

        tr.commit()
    }

    private fun findFragment(tag: String?): Fragment? {
        tag?.let {
            return supportFragmentManager.findFragmentByTag(it)
        } ?: run {
            return null
        }
    }

    private fun getActiveFragment(): Fragment? {
        supportFragmentManager.fragments.forEach {
            if (it.isVisible) {
                return it
            }
        }

        return null
    }

    private fun isInitialFragmentSelected(): Boolean {
        return bottomNavigationView.selectedItemId != initialFragmentInfo.itemId
    }

    private fun selectInitialFragment() {
        bottomNavigationView.selectedItemId = initialFragmentInfo.itemId
    }

    private fun reportShortcutUsed(id: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            shortcutManager?.reportShortcutUsed(id)
        }
    }
}