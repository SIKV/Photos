package com.github.sikv.photos.util

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
import com.github.sikv.photo.list.ui.PhotoItemLayoutType
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


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
