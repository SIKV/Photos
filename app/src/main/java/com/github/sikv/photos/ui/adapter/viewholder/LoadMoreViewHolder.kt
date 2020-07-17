package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_more.view.*

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