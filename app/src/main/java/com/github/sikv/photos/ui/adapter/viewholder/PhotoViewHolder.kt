package com.github.sikv.photos.ui.adapter.viewholder

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.model.UnsplashPhoto
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoViewHolder(
        itemView: View?,
        private val glide: RequestManager

) : RecyclerView.ViewHolder(itemView) {

    fun bind(unsplashPhoto: UnsplashPhoto?,
             clickCallback: (UnsplashPhoto, View) -> Unit,
             longClickCallback: ((UnsplashPhoto, View) -> Unit)? = null) {

        itemView.itemPhotoImage.setImageDrawable(null)
        itemView.setOnClickListener(null)

        unsplashPhoto?.let {
            glide.asBitmap()
                    .load(unsplashPhoto.urls.small)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            itemView.itemPhotoImage.setImageBitmap(resource)
                        }
                    })

            itemView.setOnClickListener {
                clickCallback.invoke(unsplashPhoto, it)
            }

            itemView.setOnLongClickListener {
                longClickCallback?.invoke(unsplashPhoto, it)
                return@setOnLongClickListener true
            }
        }
    }
}