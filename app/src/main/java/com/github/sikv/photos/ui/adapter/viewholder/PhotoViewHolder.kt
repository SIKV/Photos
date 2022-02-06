package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.sikv.photos.R
import com.github.sikv.photos.manager.PhotoLoader
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.PhotoItemLayoutType
import com.github.sikv.photos.model.getAttributionPlaceholderBackgroundColor
import com.github.sikv.photos.model.getAttributionPlaceholderTextColor
import com.github.sikv.photos.ui.adapter.OnPhotoActionListener
import com.github.sikv.photos.util.*
import kotlinx.android.synthetic.main.item_photo_full.view.*
import kotlinx.coroutines.launch

class PhotoViewHolder(
    itemView: View,
    private val photoLoader: PhotoLoader,
    private val lifecycleScope: LifecycleCoroutineScope
) : RecyclerView.ViewHolder(itemView) {

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

        itemView.photographerNameText.text = photo?.getPhotoPhotographerName()
        itemView.sourceText.text = photo?.getPhotoSource()?.title

        itemView.favoriteButton.visibility = View.VISIBLE
        itemView.favoriteButton.setImageResource(if (isFavorite) R.drawable.ic_favorite_red_24dp else R.drawable.ic_favorite_border_24dp)

        photo?.let {
            itemView.photoImage.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.CLICK, photo, view)
            }

            itemView.photoImage.setOnHoldReleaseListener(object : OnHoldReleaseListener() {
                override fun onHold(view: View) {
                    listener.onPhotoAction(OnPhotoActionListener.Action.HOLD, photo, view)
                }

                override fun onRelease(view: View) {
                    listener.onPhotoAction(OnPhotoActionListener.Action.RELEASE, photo, view)
                }
            })

            itemView.photographerImage.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.PHOTOGRAPHER, photo, view)
            }

            itemView.photographerLayout.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.PHOTOGRAPHER, photo, view)
            }

            itemView.optionsButton.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.OPTIONS, photo, view)
            }

            itemView.favoriteButton.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.FAVORITE, photo, view)
                itemView.favoriteButton.favoriteAnimation()
            }

            itemView.shareButton.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.SHARE, photo, view)
            }

            itemView.downloadButton.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.DOWNLOAD, photo, view)
            }

        } ?: run {
            itemView.optionsButton.setOnClickListener(null)
            itemView.photoImage.setOnClickListener(null)
            itemView.photoImage.setOnHoldReleaseListener(null)
            itemView.favoriteButton.setOnClickListener(null)
            itemView.downloadButton.setOnClickListener(null)
        }
    }

    private fun bindMin(photo: Photo?, listener: OnPhotoActionListener) {
        itemView.photoImage.setImageDrawable(null)

        photo?.let {
            photoLoader.load(it.getPhotoPreviewUrl(), itemView.photoImage)
        }

        photo?.let {
            itemView.photoImage.setOnClickListener { view ->
                listener.onPhotoAction(OnPhotoActionListener.Action.CLICK, photo, view)
            }

            itemView.photoImage.setOnHoldReleaseListener(object : OnHoldReleaseListener() {
                override fun onHold(view: View) {
                    listener.onPhotoAction(OnPhotoActionListener.Action.HOLD, photo, view)
                }

                override fun onRelease(view: View) {
                    listener.onPhotoAction(OnPhotoActionListener.Action.RELEASE, photo, view)
                }
            })

        } ?: run {
            itemView.photoImage.setOnClickListener(null)
            itemView.photoImage.setOnHoldReleaseListener(null)
        }
    }

    private fun loadPhotos(photo: Photo?, lifecycleScope: LifecycleCoroutineScope) {
        itemView.photoImage.setImageDrawable(null)
        itemView.photographerImage.setImageDrawable(null)

        if (photo == null) {
            return
        }

        photoLoader.load(photo.getPhotoPreviewUrl(), itemView.photoImage)

        val textColor = photo.getAttributionPlaceholderTextColor(itemView.context)
        val backgroundColor = photo.getAttributionPlaceholderBackgroundColor(itemView.context)

        lifecycleScope.launch {
            val placeholder = TextPlaceholder.with(itemView.context)
                .textFirstChar(photo.getPhotoPhotographerName())
                .textColor(textColor)
                .background(TextPlaceholder.Shape.CIRCLE, backgroundColor)
                .build()

            photoLoader.loadWithCircleCrop(
                photo.getPhotoPhotographerImageUrl(),
                placeholder,
                itemView.photographerImage
            )
        }
    }
}
