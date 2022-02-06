package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photos.manager.PhotoLoader
import com.github.sikv.photos.ui.adapter.OnPhotoActionListener
import com.github.sikv.photos.ui.adapter.PhotoGridItem
import com.github.sikv.photos.util.OnHoldReleaseListener
import com.github.sikv.photos.util.setOnHoldReleaseListener

class PhotoGridViewHolder(
    itemView: View,
    private val photoLoader: PhotoLoader
) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: PhotoGridItem, listener: OnPhotoActionListener) {
        item.items.forEach { pair ->
            val imageView = itemView.findViewById<ImageView>(pair.second)

            imageView.visibility = View.VISIBLE

            imageView.setImageDrawable(null)

            pair.first?.let { photo ->
                photoLoader.load(photo.getPhotoPreviewUrl(), imageView)

                imageView.setOnClickListener { view ->
                    listener.onPhotoAction(OnPhotoActionListener.Action.CLICK, photo, view)
                }

                imageView.setOnHoldReleaseListener(object : OnHoldReleaseListener() {
                    override fun onHold(view: View) {
                        listener.onPhotoAction(OnPhotoActionListener.Action.HOLD, photo, view)
                    }

                    override fun onRelease(view: View) {
                        listener.onPhotoAction(OnPhotoActionListener.Action.RELEASE, photo, view)
                    }
                })
            } ?: run {
                imageView.visibility = View.INVISIBLE
            }
        }
    }
}
