package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.PhotoItemClickSource
import com.github.sikv.photos.enumeration.PhotoItemLayoutType
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.PHOTO_TRANSITION_DURATION
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.util.favoriteAnimation
import kotlinx.android.synthetic.main.item_photo_full.view.*
import javax.inject.Inject

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @Inject
    lateinit var glide: RequestManager

    init {
        App.instance.appComponent.inject(this)
    }

    fun bind(itemLayoutType: PhotoItemLayoutType,
             photo: Photo?,
             favorite: Boolean,
             clickCallback: (PhotoItemClickSource, Photo, View) -> Unit) {

        if (itemLayoutType == PhotoItemLayoutType.MIN) {
            bindMin(photo, clickCallback)
            return
        }

        loadPhotos(photo)

        itemView.photographerNameText.text = photo?.getPhotoPhotographerName()
        itemView.sourceText.text = photo?.getPhotoSource()

        itemView.favoriteButton.visibility = View.VISIBLE
        itemView.favoriteButton.setImageResource(if (favorite) R.drawable.ic_favorite_red_24dp else R.drawable.ic_favorite_border_24dp)

        showCreatedAtDate(photo)
        showDescription(photo)

        photo?.let {
            itemView.photoImage.setOnClickListener { view ->
                clickCallback.invoke(PhotoItemClickSource.CLICK, it, view)
            }

            itemView.photoImage.setOnLongClickListener { view ->
                clickCallback.invoke(PhotoItemClickSource.LONG_CLICK, it, view)
                return@setOnLongClickListener true
            }

            itemView.photographerImage.setOnClickListener { view ->
                clickCallback.invoke(PhotoItemClickSource.PHOTOGRAPHER, it, view)
            }

            itemView.photographerLayout.setOnClickListener { view ->
                clickCallback.invoke(PhotoItemClickSource.PHOTOGRAPHER, it, view)
            }

            itemView.optionsButton.setOnClickListener { view ->
                clickCallback.invoke(PhotoItemClickSource.OPTIONS, it, view)
            }

            itemView.favoriteButton.setOnClickListener { view ->
                clickCallback.invoke(PhotoItemClickSource.FAVORITE, it, view)
                itemView.favoriteButton.favoriteAnimation()
            }

            itemView.shareButton.setOnClickListener { view ->
                clickCallback.invoke(PhotoItemClickSource.SHARE, it, view)
            }

            itemView.downloadButton.setOnClickListener { view ->
                clickCallback.invoke(PhotoItemClickSource.DOWNLOAD, it, view)
            }

        } ?: run {
            itemView.optionsButton.setOnClickListener(null)
            itemView.photoImage.setOnClickListener(null)
            itemView.photoImage.setOnLongClickListener(null)
            itemView.favoriteButton.setOnClickListener(null)
            itemView.downloadButton.setOnClickListener(null)
        }
    }

    private fun bindMin(photo: Photo?, clickCallback: (PhotoItemClickSource, Photo, View) -> Unit) {
        itemView.photoImage.setImageDrawable(null)

        photo?.let {
            glide.load(it.getPhotoPreviewUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(PHOTO_TRANSITION_DURATION))
                    .into(itemView.photoImage)
        }

        photo?.let {
            itemView.photoImage.setOnClickListener { view ->
                clickCallback.invoke(PhotoItemClickSource.CLICK, it, view)
            }

            itemView.photoImage.setOnLongClickListener { view ->
                clickCallback.invoke(PhotoItemClickSource.LONG_CLICK, it, view)
                return@setOnLongClickListener true
            }

        } ?: run {
            itemView.photoImage.setOnClickListener(null)
            itemView.photoImage.setOnLongClickListener(null)
        }
    }

    private fun loadPhotos(photo: Photo?) {
        itemView.photoImage.setImageDrawable(null)
        itemView.photographerImage.setImageDrawable(null)

        photo?.let {
            glide.load(it.getPhotoPreviewUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(PHOTO_TRANSITION_DURATION))
                    .into(itemView.photoImage)

            glide.load(it.getPhotoPhotographerImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(PHOTO_TRANSITION_DURATION))
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_account_circle_24dp)
                    .into(itemView.photographerImage)
        }
    }

    private fun showCreatedAtDate(photo: Photo?) {
        photo?.getPhotoCreatedAt()?.let { createdAt ->
            itemView.createdAtText.visibility = View.VISIBLE
            itemView.createdAtText.text = Utils.formatCreatedAtDate(createdAt)
        } ?: run {
            itemView.createdAtText.visibility = View.GONE
        }
    }

    private fun showDescription(photo: Photo?) {
        photo?.getPhotoDescription()?.let { description ->
            itemView.descriptionText.visibility = View.VISIBLE
            itemView.descriptionText.text = description
        } ?: run {
            itemView.descriptionText.visibility = View.GONE
        }
    }
}