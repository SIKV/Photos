package com.github.sikv.photos.ui.popup

import android.app.Activity
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import kotlinx.android.synthetic.main.popup_photo_preview.view.*

object PhotoPreviewPopup {

    fun show(activity: Activity, rootLayout: ViewGroup, photo: Photo) {
        var photoPopupPreview: PopupWindow? = null
        val layout = activity.layoutInflater.inflate(R.layout.popup_photo_preview, rootLayout, false)

        Glide.with(activity)
                .load(photo.getSmallUrl())
                .into(layout.photoPreviewImage)

        layout.photoPreviewImage.animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_in)
        layout.photoPreviewImage.animate()

        layout.setOnClickListener {
            layout.photoPreviewImage.animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_out)
            layout.photoPreviewImage.animate()

            Handler().postDelayed({
                photoPopupPreview?.dismiss()
            }, activity.resources.getInteger(R.integer.zoomAnimationsDuration).toLong())
        }

        photoPopupPreview = PopupWindow(layout,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        photoPopupPreview.showAtLocation(rootLayout, Gravity.CENTER, 0, 0)
    }
}