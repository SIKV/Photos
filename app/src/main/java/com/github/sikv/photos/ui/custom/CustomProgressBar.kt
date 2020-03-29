package com.github.sikv.photos.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import com.github.sikv.photos.R
import com.github.sikv.photos.util.AnimUtils

class CustomProgressBar : ProgressBar {

    companion object {
        private const val SET_STATE_ANIMATION_DURATION = 100L
    }

    enum class State {
        IDLE,
        ANIMATING
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setState(State.ANIMATING)
    }

    fun setState(state: State, withAnimation: Boolean = false) {
        if (withAnimation) {
            val animScale = 1.2F

            AnimUtils.getScaleAnimation(1F, animScale, 1F, animScale,
                    duration = SET_STATE_ANIMATION_DURATION).apply {

                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) { }

                    override fun onAnimationRepeat(animation: Animation?) { }

                    override fun onAnimationEnd(animation: Animation?) {
                        startAnimation(AnimUtils.getScaleAnimation(animScale, 1F, animScale, 1F,
                                duration = SET_STATE_ANIMATION_DURATION))
                    }
                })

                startAnimation(this)
            }
        }

        val drawableId = when (state) {
            State.IDLE -> R.drawable.progress_circle_filled
            State.ANIMATING -> R.drawable.progress_indeterminate
        }

        // https://stackoverflow.com/questions/18743559/progressbar-setindeterminatedrawable-doesnt-work/33917406

        val drawable = AppCompatResources.getDrawable(context, drawableId)
        val bounds = indeterminateDrawable.bounds

        indeterminateDrawable = drawable
        indeterminateDrawable.bounds = bounds
    }
}