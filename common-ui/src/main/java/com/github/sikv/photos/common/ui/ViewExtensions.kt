package com.github.sikv.photos.common.ui

import android.animation.Animator
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * View
 */
fun View.setVisibilityAnimated(newVisibility: Int, duration: Long = 500L) {
    animate()
        .alpha(if (newVisibility == View.VISIBLE) 1.0F else 0.0F)
        .setDuration(duration)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animator: Animator?) { }

            override fun onAnimationEnd(animator: Animator?) {
                if (newVisibility != View.VISIBLE) {
                    visibility = View.GONE
                }
            }

            override fun onAnimationCancel(animator: Animator?) { }

            override fun onAnimationStart(animator: Animator?) {
                if (newVisibility == View.VISIBLE) {
                    visibility = View.VISIBLE
                }
            }
        })
}

fun View.applyStatusBarsInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())

        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = insets.top
            leftMargin = insets.left
            rightMargin = insets.right
        }
        windowInsets
    }
}

/**
 * RecyclerView
 */
fun RecyclerView.scrollToTop() {
    this.smoothScrollToPosition(0)
}

fun RecyclerView.disableChangeAnimations() {
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}

/**
 * TextInputEditText
 */
fun TextInputEditText.resetErrorWhenTextChanged(parent: TextInputLayout) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            parent.error = null
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
    })
}
