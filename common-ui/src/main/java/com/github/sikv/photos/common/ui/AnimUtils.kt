package com.github.sikv.photos.common.ui

import android.view.animation.Animation
import android.view.animation.ScaleAnimation

object AnimUtils {

    fun getScaleAnimation(fromX: Float, toX: Float, fromY: Float, toY: Float, duration: Long = 200): ScaleAnimation {
        val scaleAnimation = ScaleAnimation(fromX, toX, fromY, toY,
            Animation.RELATIVE_TO_SELF, 0.5F,
            Animation.RELATIVE_TO_SELF, 0.5F)

        scaleAnimation.duration = duration

        return scaleAnimation
    }
}
