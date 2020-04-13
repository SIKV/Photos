package com.github.sikv.photos.util

import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.google.android.material.appbar.AppBarLayout

fun Fragment.customTag(): String {
    return this::class.java.simpleName
}

fun Fragment.setToolbarTitle(@StringRes title: Int) {
    view?.findViewById<TextView>(R.id.toolbarTitleText)?.setText(title)
}

fun Fragment.setToolbarTitle(title: String) {
    view?.findViewById<TextView>(R.id.toolbarTitleText)?.text = title
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

fun Fragment.showToolbarSubtitle(@StringRes subtitle: Int, @DrawableRes drawableStart: Int) {
    val subtitleTextView = view?.findViewById<TextView>(R.id.toolbarSubtitleText)

    subtitleTextView?.setText(subtitle)
    subtitleTextView?.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, 0, 0, 0)

    subtitleTextView?.visibility = View.VISIBLE
}

fun Fragment.hideToolbarSubtitle() {
    val subtitleTextView = view?.findViewById<TextView>(R.id.toolbarSubtitleText)
    subtitleTextView?.visibility = View.GONE
}

private fun setToolbarTitleWithButton(fragment: Fragment, @StringRes title: Int?, @DrawableRes navigationIcon: Int, navigationOnClickListener: () -> Unit) {
    val toolbar = fragment.view?.findViewById<Toolbar>(R.id.toolbar)
    val toolbarTitleTextView = fragment.view?.findViewById<TextView>(R.id.toolbarTitleText)

    title?.let {
        toolbarTitleTextView?.setText(it)
    }

    toolbar?.setNavigationIcon(navigationIcon)

    toolbar?.setNavigationOnClickListener {
        navigationOnClickListener()
    }
}