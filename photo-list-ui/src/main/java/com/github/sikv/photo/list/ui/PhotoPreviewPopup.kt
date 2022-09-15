package com.github.sikv.photo.list.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupWindow
import com.github.sikv.photos.common.PhotoLoader
import com.github.sikv.photos.domain.Photo

@SuppressLint("InflateParams", "ClickableViewAccessibility")
class PhotoPreviewPopup(
    private val context: Context,
    private val photoLoader: PhotoLoader
) {

    private var popupWindow: PopupWindow? = null

    private var photoPreviewCard: View? = null
    private var photoPreviewImage: ImageView? = null

    init {
        val layout = LayoutInflater.from(context)
            .inflate(R.layout.popup_photo_preview, null, false)

        popupWindow = PopupWindow(
            layout,
            calculateWidth(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        photoPreviewCard = layout.findViewById(R.id.photoPreviewCard)
        photoPreviewImage = layout.findViewById(R.id.photoPreviewImage)

        popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow?.isOutsideTouchable = false

        popupWindow?.setTouchInterceptor { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dismiss()
                true
            } else {
                false
            }
        }
    }

    fun show(parent: View, photo: Photo) {
        photoLoader.load(photo.getPhotoPreviewUrl()) { bitmap ->
            photoPreviewImage?.setImageBitmap(bitmap)

            popupWindow?.showAtLocation(parent, Gravity.CENTER, 0, 0)
            dimBackground()

            val animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
            photoPreviewCard?.startAnimation(animation)
        }
    }

    fun isShown(): Boolean = popupWindow?.isShowing ?: false

    fun dismiss() {
        val dismissAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_out)

        dismissAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                photoPreviewImage?.setImageDrawable(null)

                popupWindow?.dismiss()
            }
        })

        photoPreviewCard?.startAnimation(dismissAnimation)
    }

    private fun dimBackground() {
        popupWindow?.let { popup ->
            val rootView = popup.contentView.rootView

            val layoutParams = rootView.layoutParams as WindowManager.LayoutParams
            layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            layoutParams.dimAmount = 0.75F

            val windowManager =
                popup.contentView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.updateViewLayout(rootView, layoutParams)
        }
    }

    private fun calculateWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels - 150
    }
}
