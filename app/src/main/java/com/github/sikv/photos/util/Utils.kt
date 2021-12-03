package com.github.sikv.photos.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.R
import kotlinx.android.synthetic.main.item_option.view.*
import java.util.*

const val PHOTO_TRANSITION_DURATION = 500

object Utils {

    fun getCurrentDateAndTime(): String {
        return Calendar.getInstance().time.toString()
    }

    fun addCancelOption(context: Context?, layout: ViewGroup, cancelClickListener: View.OnClickListener) {
        val optionLayout = LayoutInflater.from(context).inflate(R.layout.item_option, layout, false)

        optionLayout.optionText.text = context?.getString(R.string.cancel)
        optionLayout.optionText.alpha = 0.5F

        optionLayout.optionSelectedImage.setImageDrawable(null)

        optionLayout.setOnClickListener(cancelClickListener)

        layout.addView(optionLayout)
    }
}
