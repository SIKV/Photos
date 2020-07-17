package com.github.sikv.photos.ui.adapter

import android.view.View
import com.github.sikv.photos.model.Photo

interface OnPhotoActionListener {

    enum class Action {
        CLICK,
        HOLD,
        RELEASE,
        PHOTOGRAPHER,
        OPTIONS,
        FAVORITE,
        SHARE,
        DOWNLOAD
    }

    fun onPhotoAction(action: Action, photo: Photo, view: View)
    fun onPhotoActionParentRelease()
}