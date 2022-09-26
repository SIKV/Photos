package com.github.sikv.photos.data.repository

import com.github.sikv.photos.domain.PhotoSource
import com.github.sikv.photos.domain.Photo

interface PhotosRepository {
    suspend fun getPhoto(id: String, source: PhotoSource): Photo?
    suspend fun getCuratedPhotos(page: Int, perPage: Int): List<Photo>
    suspend fun searchPhotos(query: String, page: Int, perPage: Int, source: PhotoSource): List<Photo>
}
