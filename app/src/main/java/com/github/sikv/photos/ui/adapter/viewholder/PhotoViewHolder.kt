package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.PHOTO_TRANSITION_DURATION
import com.github.sikv.photos.util.ViewUtils
import kotlinx.android.synthetic.main.item_photo.view.*
import javax.inject.Inject

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @Inject
    lateinit var glide: RequestManager

    init {
        App.instance.appComponent.inject(this)
    }

    fun bind(photo: Photo?,
             favorite: Boolean,
             clickCallback: (Photo, View) -> Unit,
             longClickCallback: ((Photo, View) -> Unit)? = null,
             favoriteClickCallback: ((Photo) -> Unit)? = null) {

        itemView.photoImage.setImageDrawable(null)

        itemView.setOnClickListener(null)
        itemView.favoriteButton.setOnClickListener(null)

        itemView.overlayLayout.visibility = View.VISIBLE
        itemView.favoriteButton.visibility = View.VISIBLE

        photo?.let {
            glide.load(photo.getThumbnailUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(PHOTO_TRANSITION_DURATION))
                    .into(itemView.photoImage)

            itemView.favoriteButton.setImageResource(if (favorite) R.drawable.ic_favorite_red_24dp else R.drawable.ic_favorite_border_white_24dp)

            itemView.setOnClickListener { view ->
                clickCallback.invoke(photo, view)
            }

            itemView.setOnLongClickListener { view ->
                longClickCallback?.invoke(photo, view)
                return@setOnLongClickListener true
            }

            itemView.favoriteButton.setOnClickListener {
                favoriteClickCallback?.invoke(photo)

                ViewUtils.favoriteAnimation(itemView.favoriteButton)
            }
        }
    }
}