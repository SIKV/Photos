package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photos.model.SearchTag
import com.google.android.material.chip.Chip

class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(tag: SearchTag, clickCallback: (SearchTag) -> Unit) {
        (itemView as? Chip)?.apply {
            text = tag.text

            setOnClickListener {
                clickCallback(tag)
            }
        }
    }
}