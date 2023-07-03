package com.github.sikv.photos.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun FragmentManager.getActiveRootFragment(): Fragment? {
    fragments.iterator().forEach { fragment ->
        if (fragment.isVisible) {
            return fragment
        }
    }
    return null
}

fun FragmentManager.changeFragment(hideFragmentTag: String?, showFragmentTag: String?) {
    val hideFragment = findFragmentByTag(hideFragmentTag)
    val showFragment = findFragmentByTag(showFragmentTag)

    val tr = beginTransaction()

    if (hideFragment != null) {
        tr.hide(hideFragment)
    }
    if (showFragment != null) {
        tr.show(showFragment)
    }

    tr.commit()
}
