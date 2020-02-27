package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(tag: String, clickCallback: (String) -> Unit) {
        (itemView as? Button)?.apply {
            text = tag

            setOnClickListener {
                clickCallback(tag)
            }
        }
    }
}