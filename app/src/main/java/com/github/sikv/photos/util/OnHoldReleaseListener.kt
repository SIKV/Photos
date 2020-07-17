package com.github.sikv.photos.util

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import java.util.*

fun View.setOnHoldReleaseListener(listener: OnHoldReleaseListener?) {
    setOnTouchListener(listener)
}

abstract class OnHoldReleaseListener : View.OnTouchListener {

    companion object {
        private const val LONG_HOLD_DELAY = 600L
    }

    private var actionDownTime: Long = 0

    private var longHoldTimer: Timer? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                actionDownTime =  SystemClock.elapsedRealtime()

                longHoldTimer = Timer()

                longHoldTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        Handler(Looper.getMainLooper()).post {
                            view?.let(::onHold)

                        }
                    }
                }, LONG_HOLD_DELAY)
            }

            MotionEvent.ACTION_CANCEL -> {
                longHoldTimer?.cancel()
            }

            MotionEvent.ACTION_UP -> {
                longHoldTimer?.cancel()

                if (SystemClock.elapsedRealtime() - actionDownTime > LONG_HOLD_DELAY) {
                    view?.let(::onRelease)
                }
            }
        }

        return false
    }

    abstract fun onHold(view: View)
    abstract fun onRelease(view: View)
}