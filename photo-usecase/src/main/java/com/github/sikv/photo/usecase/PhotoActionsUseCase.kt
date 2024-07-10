package com.github.sikv.photo.usecase

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.github.sikv.photos.common.ui.OptionsBottomSheetDialog
import com.github.sikv.photos.common.ui.copyText
import com.github.sikv.photos.common.ui.openUrl
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.navigation.args.SetWallpaperFragmentArguments
import com.github.sikv.photos.navigation.route.SetWallpaperRoute
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PhotoActionsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val setWallpaperRoute: SetWallpaperRoute,
    private val downloadPhotoUseCase: DownloadPhotoUseCase
) {

    fun photoAttributionClick(photo: Photo) {
        photo.getPhotoPhotographerUrl()?.let { photographerUrl ->
            context.openUrl(photographerUrl)
        } ?: run {
            context.openUrl(photo.getPhotoShareUrl())
        }
    }

    fun sharePhoto(activity: FragmentActivity, photo: Photo) {
        val intent = Intent()

        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, photo.getPhotoShareUrl())
        intent.type = "text/plain"

        activity.startActivity(intent)
    }

    fun downloadPhoto(activity: FragmentActivity, photo: Photo) {
        downloadPhotoUseCase.download(activity, photo) { message ->
            Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun setWallpaper(activity: FragmentActivity, photo: Photo) {
        setWallpaperRoute.present(activity.supportFragmentManager, SetWallpaperFragmentArguments(photo))
    }

    fun openMoreActions(activity: FragmentActivity, photo: Photo) {
        val options = listOf(
            context.getString(R.string.set_wallpaper),
            context.getString(R.string.copy_link)
        )

        val dialog = OptionsBottomSheetDialog.newInstance(options, null) { index ->
            when (index) {
                // Set Wallpaper
                0 -> {
                    setWallpaperRoute.present(activity.supportFragmentManager, SetWallpaperFragmentArguments(photo))
                }
                // Copy Link
                1 -> {
                    val label = context.getString(R.string.photo_link)
                    val text = photo.getPhotoShareUrl()

                    context.copyText(label, text)

                    Toast.makeText(activity, context.getString(R.string.link_copied), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        dialog.show(activity.supportFragmentManager)
    }
}
