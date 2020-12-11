package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.PhotoItemLayoutType
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.adapter.OnPhotoActionListener
import com.github.sikv.photos.util.*
import kotlinx.android.synthetic.main.item_photo_full.view.*

class PhotoViewHolder(
        itemView: View,
        private val glide: RequestManager
) : RecyclerView.ViewHolder(itemView) {

    fun bind(itemLayoutType: PhotoItemLayoutType,
             photo: Photo?,
             favorite: Boolean,
             listener: OnPhotoActionListener) {

        if (itemLayoutType == PhotoItemLayoutType.MIN) {
            bindMin(photo, listener)
            return
        }

        loadPhotos(photo)

        itemView.photographerNameText.text = photo?.getPhotoPhotographerName()
        itemView.sourceText.text = photo?.getPhotoSource()?.title

        itemView.favoriteButton.visibility = View.VISIBLE
        itemView.favoriteButton.setImageResource(if (favorite) R.drawable.ic_favorite_red_24dp else R.drawable.ic_favorite_border_24dp)

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
            glide.load(it.getPhotoPreviewUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(PHOTO_TRANSITION_DURATION))
                    .into(itemView.photoImage)
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

    private fun loadPhotos(photo: Photo?) {
        itemView.photoImage.setImageDrawable(null)
        itemView.photographerImage.setImageDrawable(null)

        photo?.let {
            glide.load(it.getPhotoPreviewUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(PHOTO_TRANSITION_DURATION))
                    .into(itemView.photoImage)

            TextPlaceholder.with(itemView.context)
                    .textFirstChar(it.getPhotoPhotographerName())
                    .textColor(R.color.colorText)
                    .background(TextPlaceholder.Shape.CIRCLE, R.color.colorPrimary)
                    .generateDrawable { placeholder ->
                        glide.load(it.getPhotoPhotographerImageUrl())
                                .transition(DrawableTransitionOptions.withCrossFade(PHOTO_TRANSITION_DURATION))
                                .transform(CircleCrop())
                                .placeholder(placeholder)
                                .into(itemView.photographerImage)
                    }
        }
    }
}