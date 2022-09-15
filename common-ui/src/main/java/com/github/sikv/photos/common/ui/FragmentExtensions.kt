package com.github.sikv.photos.common.ui

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

fun Fragment.setupToolbar(
    @StringRes title: Int,
    applyInsets: Boolean = true
) {
    setupToolbar(
        fragment = this,
        toolbarId = R.id.toolbar,
        title = title,
        applyInsets = applyInsets
    )
}

fun Fragment.setupToolbarWithBackButton(
    @StringRes title: Int?,
    navigationOnClickListener: () -> Unit,
    applyInsets: Boolean = true
) {
    setupToolbar(
        fragment = this,
        toolbarId = R.id.toolbar,
        title = title,
        navigationIcon = R.drawable.ic_arrow_back_24dp,
        navigationOnClick = navigationOnClickListener,
        applyInsets = applyInsets
    )
}

fun Fragment.disableScrollableToolbar() {
    (view?.findViewById<Toolbar>(R.id.toolbar)
        ?.layoutParams as? AppBarLayout.LayoutParams)?.scrollFlags = 0
}

private fun setupToolbar(
    fragment: Fragment,
    @IdRes toolbarId: Int,
    @StringRes title: Int?,
    @DrawableRes navigationIcon: Int? = null,
    navigationOnClick: (() -> Unit)? = null,
    applyInsets: Boolean = true
) {
    val toolbar = fragment.view?.findViewById<Toolbar>(toolbarId) ?: return

    title?.let(toolbar::setTitle)

    navigationIcon?.let(toolbar::setNavigationIcon)

    navigationOnClick?.let { onClick ->
        toolbar.setNavigationOnClickListener {
            onClick()
        }
    }

    if (applyInsets) {
        if (toolbar.parent is CollapsingToolbarLayout) {
            val toolbarTopParent = toolbar.parent.parent

            if (toolbarTopParent is AppBarLayout) {
                toolbarTopParent.applyStatusBarsInsets()
            }
        } else {
            toolbar.applyStatusBarsInsets()
        }
    }
}
