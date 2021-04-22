package com.github.sikv.photos.recommendation

import com.github.sikv.photos.database.dao.FavoritesDao
import com.github.sikv.photos.vision.ImageLabeler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Recommender @Inject constructor(
        private val favoritesDao: FavoritesDao,
        private val imageLabeler: ImageLabeler
) {

    data class Recommendation(
            val query: String?,
            val moreAvailable: Boolean
    )

    suspend fun getNextRecommendation(): Recommendation {
        return withContext(Dispatchers.IO) {
            favoritesDao.getRandom()?.let { randomFavorite ->
                val labels = imageLabeler.processImage(randomFavorite.getPhotoPreviewUrl())

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

    fun reset() { }
}