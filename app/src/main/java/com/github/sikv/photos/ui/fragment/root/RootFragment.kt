package com.github.sikv.photos.ui.fragment.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.github.sikv.photos.navigation.Navigation
import com.github.sikv.photos.navigation.NavigationAnimation
import com.github.sikv.photos.navigation.NavigationDispatcher
import com.github.sikv.photos.navigation.NavigationProvider

abstract class RootFragment : Fragment(), com.github.sikv.photos.navigation.NavigationProvider {

    private val navigation: com.github.sikv.photos.navigation.Navigation by lazy {
        com.github.sikv.photos.navigation.NavigationDispatcher(this, R.id.fragmentContainer)
    }

    private var delayedFragment: Fragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_root, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            provideNavigation().addFragment(provideRootFragment(), animation = com.github.sikv.photos.navigation.NavigationAnimation.NONE)

            delayedFragment?.let {
                provideNavigation().addFragment(it, animation = com.github.sikv.photos.navigation.NavigationAnimation.NONE)
            }
        }
    }

    fun addDelayedFragment(fragment: Fragment) {
        delayedFragment = fragment
    }

    override fun provideNavigation(): com.github.sikv.photos.navigation.Navigation {
        return navigation
    }

    abstract fun provideRootFragment(): Fragment
}
