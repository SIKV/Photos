package com.github.sikv.photos

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.DiffUtil
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.domain.Photo
import com.google.android.material.color.MaterialColors

fun ConfigProvider.getPagingConfig(): PagingConfig {
    val page = getPageConfig()

    return PagingConfig(
        initialLoadSize = page.initialLoadSize,
        pageSize = page.pageSize,
        enablePlaceholders = page.enablePlaceholders
    )
}
