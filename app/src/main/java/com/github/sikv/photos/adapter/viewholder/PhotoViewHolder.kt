package com.github.sikv.photos.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class PhotoViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    fun bind(i: Int) {
        itemView.findViewById<TextView>(android.R.id.text1).text = "Item $i"
    }
}