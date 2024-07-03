package com.github.sikv.photos.search

import androidx.paging.PagingData
import com.github.sikv.photos.domain.ListLayout
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.domain.PhotoSource
import kotlinx.coroutines.flow.Flow

internal data class SearchUiState(
    val query: String? = null,
    val photoSources: List<PhotoSource> = emptyList(),
    val photos: Map<PhotoSource, Flow<PagingData<Photo>>?> = emptyMap(),
    val listLayout: ListLayout = ListLayout.GRID
)
