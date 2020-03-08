package com.github.sikv.photos.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.PHOTO_TRANSITION_DURATION
import javax.inject.Inject

data class PhotoGridItem(
        @LayoutRes
        val layout: Int,
        var items: MutableList<Pair<Photo?, Int>> = mutableListOf()
)

class PhotoGridAdapter(
        private val clickCallback: (Photo, View) -> Unit,
        private val longClickCallback: ((Photo, View) -> Unit)? = null
) : RecyclerView.Adapter<PhotoGridViewHolder>() {

    private var items: List<PhotoGridItem> = emptyList()

    companion object {
        fun create(photos: List<Photo>,
                   clickCallback: (Photo, View) -> Unit,
                   longClickCallback: ((Photo, View) -> Unit)? = null): PhotoGridAdapter {

            val items: MutableList<PhotoGridItem> = mutableListOf()

            val layouts = listOf(
                    R.layout.item_photo_3_portrait_right,
                    R.layout.item_photo_3_landscape_bottom,
                    R.layout.item_photo_3_portrait_left,
                    R.layout.item_photo_3_landscape_bottom
            )

            var layoutIndex = 0

            for (i in photos.indices step 3) {
                if (layoutIndex >= layouts.size) {
                    layoutIndex = 0
                }

                val item = PhotoGridItem(layout = layouts[layoutIndex++])

                item.items.add(Pair(photos.getOrNull(i), R.id.photoBigImage))
                item.items.add(Pair(photos.getOrNull(i + 1), R.id.photo2Image))
                item.items.add(Pair(photos.getOrNull(i + 2), R.id.photo3Image))

                items.add(item)
            }

            val adapter = PhotoGridAdapter(clickCallback, longClickCallback)
            adapter.items = items

            return adapter
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return PhotoGridViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoGridViewHolder, position: Int) {
        holder.bind(items[position], clickCallback, longClickCallback)
    }
}

class PhotoGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @Inject
    lateinit var glide: RequestManager

    init {
        App.instance.appComponent.inject(this)
    }

    fun bind(item: PhotoGridItem,
             clickCallback: (Photo, View) -> Unit,
             longClickCallback: ((Photo, View) -> Unit)? = null) {

        item.items.forEach { pair ->
            val imageView = itemView.findViewById<ImageView>(pair.second)

            imageView.visibility = View.VISIBLE

            imageView.setImageDrawable(null)

            pair.first?.let { photo ->
                glide.load(photo.getThumbnailUrl())
                        .transition(DrawableTransitionOptions.withCrossFade(PHOTO_TRANSITION_DURATION))
                        .into(imageView)

                imageView.setOnClickListener { view ->
                    clickCallback.invoke(photo, view)
                }

                imageView.setOnLongClickListener { view ->
                    longClickCallback?.invoke(photo, view)
                    return@setOnLongClickListener true
                }

            } ?: run {
                imageView.visibility = View.INVISIBLE
            }
        }
    }
}