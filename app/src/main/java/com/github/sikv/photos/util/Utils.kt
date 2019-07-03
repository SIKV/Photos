package com.github.sikv.photos.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import kotlin.math.atan2
import kotlin.math.sqrt


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

        val intent = builder.build()
        intent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        intent.launchUrl(context, Uri.parse(url))
    }

    fun showSoftInput(context: Context, view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, 0)
    }

    fun hideSoftInput(context: Context, view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun dp2px(dp: Int): Int {
        App.instance?.let { app ->
            return Math.round(dp * (app.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))

        }

        return 0
    }

    fun calculateP(x: Double, y: Double, z: Double,
                   viewX: Float, viewY: Float, viewZ: Float,
                   lastGravity0: Double, lastGravity1: Double
    ): Triple<Float, Float, Pair<Double, Double>>? {

        // http://vitiy.info/how-to-create-parallax-effect-using-accelerometer

        var gX = x
        var gY = y
        var gZ = z

        var roll = 0.0
        var pitch = 0.0

        val gSum = sqrt(gX * gX + gY * gY + gZ * gZ)

        if (gSum != 0.0) {
            gX /= gSum
            gY /= gSum
            gZ /= gSum
        }

        if (gZ != 0.0) {
            roll = atan2(gX, gZ) * 180 / Math.PI
        }

        pitch = sqrt(gX * gX + gZ * gZ)

        if (pitch != 0.0) {
            pitch = atan2(gY, pitch) * 180 / Math.PI
        }

        var dgX = roll - lastGravity0
        var dgY = pitch - lastGravity1

        if (gY > 0.99) {
            dgX = 0.0
        }
        if (dgX > 180) {
            dgX = 0.0
        }
        if (dgX < -180) {
            dgX = 0.0
        }
        if (dgY > 180) {
            dgY = 0.0
        }
        if (dgY < -180) {
            dgY = 0.0
        }

        val lastGravityPair = Pair(roll, pitch)

        return if ((dgX != 0.0) || (dgY != 0.0)) {
            val newX = (viewX + dgX * (1.0 + 100.0 * viewZ)).toFloat()
            val newY = (viewY - dgY * (1.0 + 100.0 * viewZ)).toFloat()

            Triple(newX, newY, lastGravityPair)

        } else {
            null
        }
    }
}