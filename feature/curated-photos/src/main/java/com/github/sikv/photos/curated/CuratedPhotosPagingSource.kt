package com.github.sikv.photos.curated

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.sikv.photos.data.Result
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.domain.Photo

internal class CuratedPhotosPagingSource(
    private val photosRepository: PhotosRepository,
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val initialPosition = 0
        val position = params.key ?: initialPosition

        val photosResult = photosRepository.getCuratedPhotos(position, params.loadSize)

        return when (photosResult) {
            is Result.Success -> {
                LoadResult.Page(
                    data = photosResult.data,
                    prevKey = if (position == initialPosition) null else position - 1,
                    nextKey = if (photosResult.data.isEmpty()) null else position + 1
                )
            }

            is Result.Error -> {
                LoadResult.Error(photosResult.exception)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition
    }
}
