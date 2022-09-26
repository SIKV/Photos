package com.github.sikv.photo.list.ui

import android.view.View
import com.github.sikv.photos.domain.Photo

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
