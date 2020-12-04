package com.github.sikv.photos.data

import androidx.paging.PagingSource
import com.github.sikv.photos.App
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo
import javax.inject.Inject

class PexelsCuratedPhotosPagingSource : PagingSource<Int, Photo>() {

    @Inject
    lateinit var photosRepository: PhotosRepository

    init {
        App.instance.appComponent.inject(this)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val initialPosition = 0
        val position = params.key ?: initialPosition

        return try {
            val photos = photosRepository
                    .getLatestPhotos(position, params.loadSize, PhotoSource.PEXELS)

            LoadResult.Page(
                    data = photos,
                    prevKey = if (position == initialPosition) null else position - 1,
                    nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}