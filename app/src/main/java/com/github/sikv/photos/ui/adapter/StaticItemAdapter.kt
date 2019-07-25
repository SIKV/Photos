package com.github.sikv.photos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.adapter.viewholder.StaticItemViewHolder


class StaticItemAdapter(private val items: List<Item>) : RecyclerView.Adapter<StaticItemViewHolder>() {

    enum class ItemType {
        NORMAL,
        SWITCH
    }

    data class Item(
            @DrawableRes
            val iconRes: Int,

            @StringRes
            val textRes: Int,

            val type: ItemType = ItemType.NORMAL,

            val callback: () -> Unit
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaticItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_static, parent, false)
        return StaticItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: StaticItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
}