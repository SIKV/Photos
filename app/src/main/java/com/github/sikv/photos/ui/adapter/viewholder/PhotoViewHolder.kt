package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import kotlinx.android.synthetic.main.item_photo.view.*
import javax.inject.Inject

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        private const val TRANSITION_DURATION = 500
    }

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
        itemView.photographerImage.setImageDrawable(null)

        itemView.setOnClickListener(null)
        itemView.favoriteButton.setOnClickListener(null)

        itemView.overlayLayout.visibility = View.VISIBLE
        itemView.favoriteButton.visibility = View.VISIBLE

        photo?.let {
            glide.load(photo.getSmallUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(TRANSITION_DURATION))
                    .into(itemView.photoImage)

            photo.getPhotographerImageUrl()?.let { photographerImageUrl ->
                itemView.photographerImage.visibility = View.VISIBLE

                glide.load(photographerImageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade(TRANSITION_DURATION))
                        .apply(RequestOptions.circleCropTransform())
                        .into(itemView.photographerImage)

            } ?: run {
                itemView.photographerImage.visibility = View.GONE
            }

            itemView.photographerNameText.text = it.getPhotographerName()
            itemView.sourceText.text = itemView.context.getString(R.string.on_s, it.getSource())

            itemView.favoriteButton.setImageResource(if (favorite) R.drawable.ic_favorite_white_24dp else R.drawable.ic_favorite_border_white_24dp)

            itemView.setOnClickListener { view ->
                clickCallback.invoke(photo, view)
            }

            itemView.setOnLongClickListener { view ->
                longClickCallback?.invoke(photo, view)
                return@setOnLongClickListener true
            }

            itemView.favoriteButton.setOnClickListener {
                favoriteClickCallback?.invoke(photo)
            }
        }
    }
}