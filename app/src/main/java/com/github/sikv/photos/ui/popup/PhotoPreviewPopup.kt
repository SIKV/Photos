package com.github.sikv.photos.ui.popup

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import kotlinx.android.synthetic.main.popup_photo_preview.view.*
import javax.inject.Inject

class PhotoPreviewPopup {

    @Inject
    lateinit var glide: RequestManager

    init {
        App.instance.appComponent.inject(this)
    }

    fun show(activity: Activity?, rootLayout: ViewGroup, photo: Photo) {
        if (activity == null) {
            return
        }

        var photoPopupPreview: PopupWindow? = null
        val layout = activity.layoutInflater.inflate(R.layout.popup_photo_preview, rootLayout, false)

        glide.asBitmap()
                .load(photo.getPhotoPreviewUrl())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        layout.photoPreviewImage.setImageBitmap(bitmap)

                        val animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_in)
                        layout.photoPreviewCard.startAnimation(animation)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) { }
                })

        layout.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_out)

            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) { }

                override fun onAnimationRepeat(animation: Animation?) { }

                override fun onAnimationEnd(animation: Animation?) {
                    photoPopupPreview?.dismiss()
                }
            })

            layout.photoPreviewCard.startAnimation(animation)
        }

        photoPopupPreview = PopupWindow(layout,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        photoPopupPreview.isOutsideTouchable = true
        photoPopupPreview.isFocusable = true

        photoPopupPreview.showAtLocation(rootLayout, Gravity.CENTER, 0, 0)
    }
}