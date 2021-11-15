package com.github.sikv.photos.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.google.android.material.appbar.AppBarLayout

fun Fragment.setToolbarTitle(@StringRes title: Int) {
    val toolbar = view?.findViewById<Toolbar>(R.id.toolbar)
    toolbar?.setTitle(title)
}

fun Fragment.disableScrollableToolbar() {
    (view?.findViewById<Toolbar>(R.id.toolbar)?.layoutParams as? AppBarLayout.LayoutParams)?.scrollFlags = 0
}

fun Fragment.setToolbarTitleWithBackButton(@StringRes title: Int?, navigationOnClickListener: () -> Unit) {
    setToolbarTitleWithButton(this, title, R.drawable.ic_arrow_back_24dp, navigationOnClickListener)
}

fun Fragment.showToolbarBackButton(navigationOnClickListener: () -> Unit) {
    view?.findViewById<Toolbar>(R.id.toolbar)?.run {
        setNavigationIcon(R.drawable.ic_arrow_back_24dp)

        setNavigationOnClickListener {
            navigationOnClickListener()
        }
    }
}

private fun setToolbarTitleWithButton(fragment: Fragment, @StringRes title: Int?, @DrawableRes navigationIcon: Int, navigationOnClickListener: () -> Unit) {
    val toolbar = fragment.view?.findViewById<Toolbar>(R.id.toolbar)

    title?.let {
        toolbar?.setTitle(it)
    }

    toolbar?.setNavigationIcon(navigationIcon)

    toolbar?.setNavigationOnClickListener {
        navigationOnClickListener()
    }
}
