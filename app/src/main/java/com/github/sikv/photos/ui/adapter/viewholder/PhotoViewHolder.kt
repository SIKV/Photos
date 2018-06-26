package com.github.sikv.photos.ui.adapter.viewholder

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.model.Photo
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoViewHolder(
        itemView: View?,
        private val glide: RequestManager

) : RecyclerView.ViewHolder(itemView) {

    fun bind(photo: Photo?,
             clickCallback: (Photo, View) -> Unit,
             longClickCallback: ((Photo, View) -> Unit)? = null) {

//        itemView.itemPhotoShimmerLayout.visibility = View.VISIBLE

        itemView.itemPhotoImage.setImageDrawable(null)
        itemView.setOnClickListener(null)

        itemView.itemPhotoShimmerLayout.visibility = View.GONE

        photo?.let {
            glide.asBitmap()
                    .load(photo.urls.small)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                            itemView.itemPhotoImage.setImageBitmap(resource)
//                            itemView.itemPhotoShimmerLayout.visibility = View.GONE

                        }
                    })

            itemView.setOnClickListener {
                clickCallback.invoke(photo, it)
            }

            itemView.setOnLongClickListener {
                longClickCallback?.invoke(photo, it)
                return@setOnLongClickListener true
            }
        }
    }
}