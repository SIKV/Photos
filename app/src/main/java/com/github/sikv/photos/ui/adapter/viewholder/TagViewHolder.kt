package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tag.view.*

class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(tag: String) {
        itemView.tagText.text = tag
    }
}