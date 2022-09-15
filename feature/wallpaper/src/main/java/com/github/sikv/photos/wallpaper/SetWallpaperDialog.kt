package com.github.sikv.photos.wallpaper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.common.PhotoLoader
import com.github.sikv.photos.common.ui.addCancelOption
import com.github.sikv.photos.navigation.args.SetWallpaperFragmentArguments
import com.github.sikv.photos.navigation.args.fragmentArguments
import com.github.sikv.photos.wallpaper.databinding.LayoutSetWallpaperBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetWallpaperDialog : BottomSheetDialogFragment() {

    @Inject
    lateinit var photoLoader: PhotoLoader

    private val args by fragmentArguments<SetWallpaperFragmentArguments>()

    private val wallpaperService: WallpaperService by lazy {
        WallpaperService(
            context = requireActivity(),
            photoLoader = photoLoader,
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
    ): View {
        val view = inflater.inflate(R.layout.layout_bottom_sheet, container, false)

        val rootLayout = view.findViewById<ViewGroup>(R.id.rootLayout)
        _binding = LayoutSetWallpaperBinding.inflate(layoutInflater, rootLayout, false)

        rootLayout.addView(binding.root)
        addCancelOption(context, rootLayout) { dismiss() }

        wallpaperService.setWallpaper(args.photo)
        binding.tryAgainButton.setOnClickListener { wallpaperService.setWallpaper(args.photo) }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
