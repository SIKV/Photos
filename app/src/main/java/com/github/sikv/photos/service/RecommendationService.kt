package com.github.sikv.photos.service

import com.github.sikv.photos.database.dao.FavoritesDao
import com.github.sikv.photos.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class RecommendedPhotos(
    val photos: List<Photo>,
    val moreAvailable: Boolean,
    val reset: Boolean
)

class RecommendationService @Inject constructor(
    private val favoritesDao: FavoritesDao,
    private val imageLabelerService: ImageLabelerService
) {

    data class Recommendation(
        val query: String?,
        val moreAvailable: Boolean
    )

    suspend fun getNextRecommendation(): Recommendation {
        return withContext(Dispatchers.IO) {
            favoritesDao.getRandom()?.let { randomFavorite ->
                val labels = imageLabelerService.processImage(randomFavorite.getPhotoPreviewUrl())

                if (labels.isEmpty()) {
                    Recommendation(null, true)
                } else {
                    Recommendation(labels.random(), true)
                }
            } ?: run {
                Recommendation(null, false)
            }
        }
    }

    fun reset() {}
}
