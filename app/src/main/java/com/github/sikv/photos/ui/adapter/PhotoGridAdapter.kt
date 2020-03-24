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
import com.github.sikv.photos.enumeration.PhotoItemClickSource
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.PHOTO_TRANSITION_DURATION
import kotlinx.android.synthetic.main.item_load_more.view.*
import javax.inject.Inject

data class PhotoGridItem(
        @LayoutRes
        val layout: Int,
        var items: MutableList<Pair<Photo?, Int>> = mutableListOf()
)

class PhotoGridAdapter(
        private val clickCallback: (PhotoItemClickSource, Photo, View) -> Unit,
        private val loadMoreCallback: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ITEM_VIEW_TYPE_LOAD_MORE = R.layout.item_load_more
    }

    private var items: MutableList<PhotoGridItem> = mutableListOf()

    private var showLoadMoreOption: Boolean = false
    private var loadingMoreInProgress: Boolean = false

    fun addItems(photos: List<Photo>, showLoadMoreOption: Boolean = true) {
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

        this.items.addAll(items)

        this.showLoadMoreOption = showLoadMoreOption
        this.loadingMoreInProgress = false

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size + if (showLoadMoreOption) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (showLoadMoreOption && position >= items.size) {
            ITEM_VIEW_TYPE_LOAD_MORE
        } else {
            items[position].layout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return if (viewType == ITEM_VIEW_TYPE_LOAD_MORE) {
            LoadMoreViewHolder(view)
        } else {
            PhotoGridViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadMoreViewHolder) {
            holder.bind(loadingMoreInProgress) {
                loadingMoreInProgress = true
                loadMoreCallback()
            }
        } else if (holder is PhotoGridViewHolder) {
            holder.bind(items[position], clickCallback)
        }
    }
}

class PhotoGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @Inject
    lateinit var glide: RequestManager

    init {
        App.instance.appComponent.inject(this)
    }

    fun bind(item: PhotoGridItem, clickCallback: (PhotoItemClickSource, Photo, View) -> Unit) {
        item.items.forEach { pair ->
            val imageView = itemView.findViewById<ImageView>(pair.second)

            imageView.visibility = View.VISIBLE

            imageView.setImageDrawable(null)

            pair.first?.let { photo ->
                glide.load(photo.getPhotoPreviewUrl())
                        .transition(DrawableTransitionOptions.withCrossFade(PHOTO_TRANSITION_DURATION))
                        .into(imageView)

                imageView.setOnClickListener { view ->
                    clickCallback.invoke(PhotoItemClickSource.CLICK, photo, view)
                }

                imageView.setOnLongClickListener { view ->
                    clickCallback.invoke(PhotoItemClickSource.LONG_CLICK, photo, view)
                    return@setOnLongClickListener true
                }

            } ?: run {
                imageView.visibility = View.INVISIBLE
            }
        }
    }
}

class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(loadingMoreInProgress: Boolean, loadMoreCallback: () -> Unit) {
        itemView.loadMoreButton.visibility = if (loadingMoreInProgress) View.GONE else View.VISIBLE
        itemView.progressBar.visibility = if (loadingMoreInProgress) View.VISIBLE else View.GONE

        itemView.loadMoreButton.setOnClickListener { view ->
            itemView.progressBar.visibility = View.VISIBLE
            view.visibility = View.GONE

            loadMoreCallback()
        }
    }
}