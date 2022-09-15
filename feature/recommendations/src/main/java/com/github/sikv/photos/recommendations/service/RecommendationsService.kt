package com.github.sikv.photos.recommendations.service

import com.github.sikv.photos.data.repository.FavoritesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecommendationsService @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val imageLabelerService: ImageLabelerService
) {

    data class Recommendation(
        val query: String?,
        val moreAvailable: Boolean
    )

    suspend fun getNextRecommendation(): Recommendation {
        return withContext(Dispatchers.IO) {
            favoritesRepository.getRandom()?.let { randomFavorite ->
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
