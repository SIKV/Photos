package com.github.sikv.photos.util

import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.github.sikv.photos.R


object Utils {

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

    fun navigationBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")

        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    fun openUrl(context: Context, url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))

        builder.build().launchUrl(context, Uri.parse(url))
    }

    fun hideSoftInput(context: Context, view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}