package com.github.sikv.photo.list.ui.adapter

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photo.list.ui.*
import com.github.sikv.photos.common.PhotoLoader
import com.github.sikv.photos.common.ui.TextPlaceholder
import com.github.sikv.photos.common.ui.getAttributionPlaceholderBackgroundColor
import com.github.sikv.photos.common.ui.getAttributionPlaceholderTextColor
import com.github.sikv.photos.domain.Photo
import kotlinx.coroutines.launch

class PhotoViewHolder(
    itemView: View,
    private val photoLoader: PhotoLoader,
    private val lifecycleScope: LifecycleCoroutineScope
) : RecyclerView.ViewHolder(itemView) {

    private val photographerNameText = itemView.findViewById<TextView>(R.id.photographerNameText)
    private val sourceText = itemView.findViewById<TextView>(R.id.sourceText)
    private val favoriteButton = itemView.findViewById<ImageButton>(R.id.favoriteButton)
    private val photoImage = itemView.findViewById<ImageView>(R.id.photoImage)
    private val photographerImage = itemView.findViewById<ImageView>(R.id.photographerImage)
    private val photographerLayout = itemView.findViewById<View>(R.id.photographerLayout)
    private val optionsButton = itemView.findViewById<ImageButton>(R.id.optionsButton)
    private val shareButton = itemView.findViewById<ImageButton>(R.id.shareButton)
    private val downloadButton = itemView.findViewById<ImageButton>(R.id.downloadButton)

    fun bind(
        itemLayoutType: PhotoItemLayoutType,
        photo: Photo?,
        isFavorite: Boolean,
        listener: OnPhotoActionListener
    ) {
        if (itemLayoutType == PhotoItemLayoutType.MIN) {
            bindMin(photo, listener)
            return
        }

        loadPhotos(photo, lifecycleScope)

        photographerNameText.text = photo?.getPhotoPhotographerName()
        sourceText.text = photo?.getPhotoSource()?.title

        favoriteButton.visibility = View.VISIBLE
        favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_red_24dp else R.drawable.ic_favorite_border_24dp
        )

        photo?.let {
            photoImage.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.CLICK, photo, view)
            }

            photoImage.setOnHoldReleaseListener(object : OnHoldReleaseListener() {
                override fun onHold(view: View) {
                    listener.onPhotoAction(OnPhotoActionListener.Action.HOLD, photo, view)
                }

                override fun onRelease(view: View) {
                    listener.onPhotoAction(OnPhotoActionListener.Action.RELEASE, photo, view)
                }
            })

            photographerImage.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.PHOTOGRAPHER, photo, view)
            }

            photographerLayout.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.PHOTOGRAPHER, photo, view)
            }

            optionsButton.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.OPTIONS, photo, view)
            }

            favoriteButton.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.FAVORITE, photo, view)
                favoriteButton.favoriteAnimation()
            }

            shareButton.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.SHARE, photo, view)
            }

            downloadButton.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.DOWNLOAD, photo, view)
            }

        } ?: run {
            optionsButton.setOnClickListener(null)
            photoImage.setOnClickListener(null)
            photoImage.setOnHoldReleaseListener(null)
            favoriteButton.setOnClickListener(null)
            downloadButton.setOnClickListener(null)
        }
    }

    private fun bindMin(photo: Photo?, listener: OnPhotoActionListener) {
        photoImage.setImageDrawable(null)

        photo?.let {
            photoLoader.load(it.getPhotoPreviewUrl(), photoImage)
        }

        photo?.let {
            photoImage.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.CLICK, photo, view)
            }

            photoImage.setOnHoldReleaseListener(object : OnHoldReleaseListener() {
                override fun onHold(view: View) {
                    listener.onPhotoAction(OnPhotoActionListener.Action.HOLD, photo, view)
                }

                override fun onRelease(view: View) {
                    listener.onPhotoAction(OnPhotoActionListener.Action.RELEASE, photo, view)
                }
            })

        } ?: run {
            photoImage.setOnClickListener(null)
            photoImage.setOnHoldReleaseListener(null)
        }
    }

    private fun loadPhotos(photo: Photo?, lifecycleScope: LifecycleCoroutineScope) {
        photoImage.setImageDrawable(null)
        photographerImage.setImageDrawable(null)

        if (photo == null) {
            return
        }

        photoLoader.load(photo.getPhotoPreviewUrl(), photoImage)

        val textColor = getAttributionPlaceholderTextColor(itemView.context)
        val backgroundColor = getAttributionPlaceholderBackgroundColor(itemView.context)

        lifecycleScope.launch {
            val placeholder = TextPlaceholder.with(itemView.context)
                .textFirstChar(photo.getPhotoPhotographerName())
                .textColor(textColor)
                .background(TextPlaceholder.Shape.CIRCLE, backgroundColor)
                .build()

            photoLoader.loadWithCircleCrop(
                photo.getPhotoPhotographerImageUrl(),
                placeholder,
                photographerImage
            )
        }
    }
}
