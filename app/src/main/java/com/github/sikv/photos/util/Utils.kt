package com.github.sikv.photos.util

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.TextView


object Utils {

    fun log(msg: String) {
        Log.i("TAG", msg)
    }

    fun px2dp(context: Context, px: Int): Int {
        return px / context.resources.displayMetrics.density.toInt()
    }

    fun makeClickable(textView: TextView, clickable: Array<String>, clickableSpans: Array<ClickableSpan>) {
        val spannableString = SpannableString(textView.text)

        for (i in clickable.indices) {
            val clickableSpan = clickableSpans[i]
            val str = clickable[i]

            val startIndexOfLink = textView.text.toString().indexOf(str)

            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + str.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    fun makeUnderlineBold(textView: TextView, bold: Array<String>) {
        val spannableString = SpannableString(textView.text)

        for (i in bold.indices) {
            val str = bold[i]

            val startIndexOfLink = textView.text.toString().indexOf(str)

            spannableString.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    startIndexOfLink, startIndexOfLink + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            spannableString.setSpan(UnderlineSpan(),
                    startIndexOfLink, startIndexOfLink + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}