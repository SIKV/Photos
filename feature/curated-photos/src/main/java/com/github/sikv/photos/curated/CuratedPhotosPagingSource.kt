package com.github.sikv.photos.curated

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.domain.Photo

internal class CuratedPhotosPagingSource(
    private val photosRepository: PhotosRepository,
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val initialPosition = 0
        val position = params.key ?: initialPosition

        return try {
            val photos = photosRepository.getCuratedPhotos(position, params.loadSize)

            LoadResult.Page(
                data = photos,
                prevKey = if (position == initialPosition) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition
    }
}
