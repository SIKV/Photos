package com.github.sikv.photos.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.R
import com.github.sikv.photos.databinding.ItemOptionBinding
import java.util.*

object Utils {

    fun getCurrentDateAndTime(): String {
        return Calendar.getInstance().time.toString()
    }

    fun addCancelOption(context: Context?, layout: ViewGroup, cancelClickListener: View.OnClickListener) {
        val binding = ItemOptionBinding.inflate(LayoutInflater.from(context), layout, false)

        binding.optionText.text = context?.getString(R.string.cancel)
        binding.optionText.alpha = 0.5F

        binding.optionSelectedImage.setImageDrawable(null)

        binding.root.setOnClickListener(cancelClickListener)

        layout.addView(binding.root)
    }
}
