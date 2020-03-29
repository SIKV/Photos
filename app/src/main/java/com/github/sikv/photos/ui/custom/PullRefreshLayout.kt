package com.github.sikv.photos.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photos.R

class PullRefreshLayout : FrameLayout {

    companion object {
        private const val CHILD_TRANSLATION_Y_ANIMATION_DURATION = 150L
    }

    private enum class State {
        IDLE,
        VISIBLE,
        LOADING
    }

    private var state = State.IDLE
        set(value) {
            field = value

            when (field) {
                State.IDLE -> {
                    setProgressBarAnimating(false)
                    setChildTranslationYAnimated(0F)
                }

                State.LOADING -> {
                    setProgressBarAnimating(true)
                    onRefresh?.invoke()
                }

                else -> { }
            }
        }

    private var childOnTop = false

    private var actionDownRawY = 0F
    private var yBegin = 0F

    private var progressBar: CustomProgressBar
    private var progressHiderView: View

    var onRefresh: (() -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val progressLayout = LayoutInflater.from(context).inflate(R.layout.layout_pull_refresh, this, false)

        progressBar = progressLayout.findViewById(R.id.progressBar)
        progressHiderView = progressLayout.findViewById(R.id.progressHiderView)

        addView(progressLayout)

        setProgressBarAnimating(false)
    }

    fun finishRefreshing() {
        state = State.IDLE
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)

        if (child is RecyclerView) {
            child.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    recyclerView.computeVerticalScrollOffset().let {
                        childOnTop = it == 0
                    }
                }
            })
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        var handleTouchEvent = super.onInterceptTouchEvent(event)

        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                actionDownRawY = event.rawY
                yBegin = event.rawY - getChildTranslationY()
            }

            MotionEvent.ACTION_MOVE -> {
                if (actionDownRawY < event.rawY) {
                    if (childOnTop) {
                        handleTouchEvent = true
                    }
                }
            }

            else -> return super.onInterceptTouchEvent(event)
        }

        return handleTouchEvent
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                setChildTranslationY(event.rawY - yBegin)

                if (getChildTranslationY() >= getProgressViewBottom()) {
                    state = State.VISIBLE
                }
            }

            MotionEvent.ACTION_UP -> {
                val progressViewBottom = getProgressViewBottom()

                if (getChildTranslationY() >= progressViewBottom && state != State.IDLE) {
                    setChildTranslationYAnimated(progressViewBottom)

                    state = State.LOADING

                } else {
                    setChildTranslationYAnimated(0F)
                }
            }

            else -> return super.onInterceptTouchEvent(event)
        }

        return super.onTouchEvent(event)
    }

    private fun setProgressBarAnimating(animating: Boolean) {
        if (animating) {
            progressBar.setState(CustomProgressBar.State.ANIMATING, withAnimation = true)
        } else {
            progressBar.setState(CustomProgressBar.State.IDLE)
        }
    }

    private fun getProgressViewBottom(): Float {
        return getChildAt(0).bottom.toFloat()
    }

    private fun getChildTranslationY(): Float {
        return getChildAt(1).translationY
    }

    private fun setChildTranslationY(translationY: Float) {
        val child = getChildAt(1)

        child.translationY = translationY
        progressHiderView.translationY = translationY
    }

    private fun setChildTranslationYAnimated(translationY: Float) {
        val child = getChildAt(1)

        val animator = ValueAnimator.ofFloat(getChildTranslationY(), translationY)
        animator.duration = CHILD_TRANSLATION_Y_ANIMATION_DURATION

        animator.addUpdateListener {
            val value = it.animatedValue as Float

            child.translationY = value
            progressHiderView.translationY = value
        }

        animator.start()
    }
}