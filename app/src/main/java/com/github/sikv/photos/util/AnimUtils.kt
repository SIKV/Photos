package com.github.sikv.photos.util

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View

object AnimUtils {

    const val DURATION_NORMAL = 150L

    fun animateX(view: View, x: Float, duration: Long = DURATION_NORMAL, listener: Animator.AnimatorListener? = null) {
        val anim = ObjectAnimator.ofFloat(view, "x", x)

        anim.duration = duration

        listener?.let {
            anim.addListener(it)
        }

        anim.start()
    }

    fun animateY(view: View, y: Float, duration: Long = DURATION_NORMAL, listener: Animator.AnimatorListener? = null) {
        val anim = ObjectAnimator.ofFloat(view, "y", y)

        anim.duration = duration

        listener?.let {
            anim.addListener(it)
        }

        anim.start()
    }
}