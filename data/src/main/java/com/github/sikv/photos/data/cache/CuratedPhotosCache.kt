package com.github.sikv.photos.data.cache

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.sikv.photos.data.PhotosDTO
import com.github.sikv.photos.data.serializer.PhotosDTOSerializer
import com.github.sikv.photos.domain.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import toDTO
import toDomain
import javax.inject.Inject

private val Context.curatedPhotosStore : DataStore<PhotosDTO> by dataStore(
    fileName = "curated_photos_cache_store",
    serializer = PhotosDTOSerializer
)

class CuratedPhotosCache @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val curatedPhotosStore: DataStore<PhotosDTO> = context.curatedPhotosStore

    suspend fun getPhotos(): List<Photo> {
        val photosDTO = curatedPhotosStore.data.firstOrNull()
        return photosDTO?.toDomain() ?: emptyList()
    }

    suspend fun update(photos: List<Photo>) {
        try {
            curatedPhotosStore.updateData {
                photos.toDTO()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
