package com.github.sikv.photos.util

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.github.sikv.photos.R
import com.google.android.material.appbar.AppBarLayout

object ViewUtils {

    fun setToolbarTitle(fragment: Fragment, @StringRes title: Int) {
        fragment.view?.findViewById<TextView>(R.id.toolbarTitleText)?.setText(title)
    }

    fun disableScrollableToolbar(fragment: Fragment) {
        (fragment.view?.findViewById<Toolbar>(R.id.toolbar)?.layoutParams as? AppBarLayout.LayoutParams)?.scrollFlags = 0
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

    fun favoriteAnimation(view: View) {
        val favoriteAnimationDuration = 200L

        val scaleAnimation = ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)

        scaleAnimation.duration = favoriteAnimationDuration
        view.startAnimation(scaleAnimation)
    }

    fun disableChangeAnimations(recyclerView: RecyclerView) {
        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }
}