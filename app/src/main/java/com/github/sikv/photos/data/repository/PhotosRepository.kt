package com.github.sikv.photos.data.repository

import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo

interface PhotosRepository {
    suspend fun getPhoto(id: String, source: PhotoSource): Photo?
    suspend fun getLatestPhotos(page: Int, perPage: Int, source: PhotoSource): List<Photo>
    suspend fun searchPhotos(query: String, page: Int, perPage: Int, source: PhotoSource): List<Photo>
    suspend fun searchPhotos(query: String, limit: Int): List<Photo>
}