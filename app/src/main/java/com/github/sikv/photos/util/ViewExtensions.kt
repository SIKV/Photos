package com.github.sikv.photos.util

import android.animation.Animator
import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.github.sikv.photos.enumeration.PhotoItemLayoutType
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * View
 */
fun View.changeVisibilityWithAnimation(visibility: Int) {
    val duration = 100L

    if (visibility == View.VISIBLE) {
        val animation = AnimUtils.getScaleAnimation(0F, 1.1F, 0F, 1.1F, duration = duration)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                this@changeVisibilityWithAnimation.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) { }

            override fun onAnimationEnd(animation: Animation?) { }
        })

        startAnimation(animation)

    } else {
        val animation = AnimUtils.getScaleAnimation(1F, 0F, 1F, 0F, duration = duration)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) { }

            override fun onAnimationRepeat(animation: Animation?) { }

            override fun onAnimationEnd(animation: Animation?) {
                this@changeVisibilityWithAnimation.visibility = View.INVISIBLE
            }
        })

        startAnimation(animation)
    }
}

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

fun View.favoriteAnimation() {
    startAnimation(AnimUtils.getScaleAnimation(0F, 1.1F, 0F, 1.1F))
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

fun RecyclerView.setItemLayoutType(itemLayoutType: PhotoItemLayoutType) {
    layoutManager = GridLayoutManager(context, itemLayoutType.spanCount)
    setPadding(0, itemLayoutType.recyclerVerticalPadding, 0, itemLayoutType.recyclerVerticalPadding)

    // Invalidate view holders
    adapter = adapter
}

/**
 * TextView
 */
fun TextView.makeClickable(clickable: Array<String>, clickableSpans: Array<ClickableSpan>) {
    val spannableString = SpannableString(text)

    for (i in clickable.indices) {
        val clickableSpan = clickableSpans[i]
        val str = clickable[i]

        val startIndexOfLink = text.toString().indexOf(str)

        spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + str.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    movementMethod = LinkMovementMethod.getInstance()
    setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun TextView.makeUnderlineBold(bold: Array<String>) {
    val spannableString = SpannableString(text)

    for (i in bold.indices) {
        val str = bold[i]

        val startIndexOfLink = text.toString().indexOf(str)

        spannableString.setSpan(StyleSpan(Typeface.BOLD),
                startIndexOfLink, startIndexOfLink + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableString.setSpan(UnderlineSpan(),
                startIndexOfLink, startIndexOfLink + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    movementMethod = LinkMovementMethod.getInstance()
    setText(spannableString, TextView.BufferType.SPANNABLE)
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