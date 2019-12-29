package com.github.sikv.photos.util

import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R

object ViewUtils {

    fun setToolbarTitle(fragment: Fragment, @StringRes title: Int) {
        fragment.view?.findViewById<TextView>(R.id.toolbarTitleText)?.setText(title)
    }

    fun setToolbarTitleWithBackButton(fragment: Fragment, @StringRes title: Int, navigationOnClickListener: () -> Unit) {
        setToolbarTitleWithButton(fragment, title, R.drawable.ic_arrow_back_24dp, navigationOnClickListener)
    }

    private fun setToolbarTitleWithButton(fragment: Fragment, @StringRes title: Int, @DrawableRes navigationIcon: Int, navigationOnClickListener: () -> Unit) {
        val toolbar = fragment.view?.findViewById<Toolbar>(R.id.toolbar)
        val toolbarTitleTextView = fragment.view?.findViewById<TextView>(R.id.toolbarTitleText)

        toolbarTitleTextView?.setText(title)
        toolbar?.setNavigationIcon(navigationIcon)

        toolbar?.setNavigationOnClickListener {
            navigationOnClickListener()
        }
    }

    fun showToolbarSubtitle(fragment: Fragment, @StringRes subtitle: Int, @DrawableRes drawableStart: Int) {
        val subtitleTextView = fragment.view?.findViewById<TextView>(R.id.toolbarSubtitleText)

        subtitleTextView?.setText(subtitle)
        subtitleTextView?.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, 0, 0, 0)

        subtitleTextView?.visibility = View.VISIBLE
    }

    fun hideToolbarSubtitle(fragment: Fragment) {
        val subtitleTextView = fragment.view?.findViewById<TextView>(R.id.toolbarSubtitleText)
        subtitleTextView?.visibility = View.GONE
    }
}