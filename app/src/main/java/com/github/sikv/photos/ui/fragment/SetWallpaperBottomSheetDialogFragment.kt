package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.R
import kotlinx.android.synthetic.main.bottom_sheet_set_wallpaper.view.*

class SetWallpaperBottomSheetDialogFragment : BottomSheetDialogFragment() {

    interface Callback {
        fun setHomeScreen()
        fun setLockScreen()
        fun setHomeAndLockScreen()
    }

    var callback: Callback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_set_wallpaper, container, false)

        view.setWallpaperHomeScreenButton.setOnClickListener {
            callback?.setHomeScreen()
        }

        view.setWallpaperLockScreenButton.setOnClickListener {
            callback?.setLockScreen()
        }

        view.setWallpaperBothButton.setOnClickListener {
            callback?.setHomeAndLockScreen()
        }

        return view
    }
}