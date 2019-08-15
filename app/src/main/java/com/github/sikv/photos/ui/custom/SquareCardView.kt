package com.github.sikv.photos.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView

class SquareCardView(context: Context, attrs: AttributeSet?) : CardView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}