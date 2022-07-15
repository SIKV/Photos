package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photos.R

class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val loadMoreButton = itemView.findViewById<View>(R.id.loadMoreButton)
    private val progressBar = itemView.findViewById<View>(R.id.progressBar)

    fun bind(loadingMoreInProgress: Boolean, loadMoreCallback: () -> Unit) {
        loadMoreButton.visibility = if (loadingMoreInProgress) View.GONE else View.VISIBLE
        progressBar.visibility = if (loadingMoreInProgress) View.VISIBLE else View.GONE

        loadMoreButton.setOnClickListener { view ->
            progressBar.visibility = View.VISIBLE
            view.visibility = View.GONE

            loadMoreCallback()
        }
    }
}
