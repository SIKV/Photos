package com.github.sikv.photo.usecase

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.github.sikv.photos.common.ui.OptionsBottomSheetDialog
import com.github.sikv.photos.common.ui.copyText
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.navigation.args.SetWallpaperFragmentArguments
import com.github.sikv.photos.navigation.route.SetWallpaperRoute
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PhotoActionsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val setWallpaperRoute: SetWallpaperRoute
) {

    fun openActions(
        fragmentManager: FragmentManager,
        photo: Photo,
        onShowMessage: (String) -> Unit
    ) {
        val options = listOf(
            context.getString(R.string.set_wallpaper),
            context.getString(R.string.copy_link)
        )

        val dialog = OptionsBottomSheetDialog.newInstance(options, null) { index ->
            when (index) {
                // Set Wallpaper
                0 -> {
                    setWallpaperRoute.present(fragmentManager, SetWallpaperFragmentArguments(photo))
                }
                // Copy Link
                1 -> {
                    val label = context.getString(R.string.photo_link)
                    val text = photo.getPhotoShareUrl()

                    context.copyText(label, text)
                    onShowMessage(context.getString(R.string.link_copied))
                }
            }
        }

        dialog.show(fragmentManager)
    }
}
