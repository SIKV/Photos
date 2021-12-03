package com.github.sikv.photos.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.R
import com.github.sikv.photos.databinding.LayoutSetWallpaperBinding
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.service.WallpaperService
import com.github.sikv.photos.util.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetWallpaperDialog : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(photo: Photo): SetWallpaperDialog = SetWallpaperDialog()
            .apply {
                arguments = bundleOf(Photo.KEY to photo)
            }
    }

    @Inject
    lateinit var glide: RequestManager

    private val wallpaperService: WallpaperService by lazy {
        WallpaperService(
            context = requireActivity(),
            glide = glide,
            onDownloading = {
                binding.statusText.setText(R.string.downloading_photo)

                binding.progressBar.visibility = View.VISIBLE
                binding.tryAgainButton.visibility = View.GONE
            },
            onReady = {
                binding.statusText.setText(R.string.setting_wallpaper)

                binding.progressBar.visibility = View.VISIBLE
                binding.tryAgainButton.visibility = View.GONE

                dismiss()
            },
            onError = {
                binding.statusText.setText(R.string.error_downloading_photo)

                binding.progressBar.visibility = View.GONE
                binding.tryAgainButton.visibility = View.VISIBLE
            }
        )
    }

    private var _binding: LayoutSetWallpaperBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_bottom_sheet, container, false)

        val rootLayout = view.findViewById<ViewGroup>(R.id.rootLayout)
        _binding = LayoutSetWallpaperBinding.inflate(layoutInflater, rootLayout, false)

        rootLayout.addView(binding.root)
        Utils.addCancelOption(context, rootLayout) { dismiss() }

        val photo = requireNotNull(arguments?.getParcelable<Photo>(Photo.KEY))

        wallpaperService.setWallpaper(photo)
        binding.tryAgainButton.setOnClickListener { wallpaperService.setWallpaper(photo) }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, "")
    }
}
