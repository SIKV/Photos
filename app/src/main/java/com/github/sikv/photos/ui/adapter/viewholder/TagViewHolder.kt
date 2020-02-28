package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photos.model.SearchTag

class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(tag: SearchTag, clickCallback: (SearchTag) -> Unit) {
        (itemView as? Button)?.apply {
            text = tag.text

            setOnClickListener {
                clickCallback(tag)
            }
        }
    }
}