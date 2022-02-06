package com.github.sikv.photos.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photos.R
import com.github.sikv.photos.manager.PhotoLoader
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.adapter.viewholder.LoadMoreViewHolder
import com.github.sikv.photos.ui.adapter.viewholder.PhotoGridViewHolder

data class PhotoGridItem(
    @LayoutRes
    val layout: Int,
    var items: MutableList<Pair<Photo?, Int>> = mutableListOf()
)

class PhotoGridAdapter(
    private val photoLoader: PhotoLoader,
    private val listener: OnPhotoActionListener,
    private val loadMoreCallback: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ITEM_VIEW_TYPE_LOAD_MORE = R.layout.item_load_more
    }

    private var items: MutableList<PhotoGridItem> = mutableListOf()

    private var showLoadMoreOption: Boolean = false
    private var loadingMoreInProgress: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                listener.onPhotoActionParentRelease()
            }

            return@setOnTouchListener false
        }
    }

    fun addItems(photos: List<Photo>, showLoadMoreOption: Boolean) {
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

    fun clear() {
        items.clear()
    }

    fun hasItems() = items.isNotEmpty()

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
            PhotoGridViewHolder(view, photoLoader)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadMoreViewHolder) {
            holder.bind(loadingMoreInProgress) {
                loadingMoreInProgress = true
                loadMoreCallback()
            }
        } else if (holder is PhotoGridViewHolder) {
            holder.bind(items[position], listener)
        }
    }
}
