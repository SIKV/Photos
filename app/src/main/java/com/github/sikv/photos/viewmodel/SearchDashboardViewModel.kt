package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sikv.photos.config.ListConfig
import com.github.sikv.photos.config.feature.FeatureFlag
import com.github.sikv.photos.config.feature.FeatureFlagProvider
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.service.RecommendationService
import com.github.sikv.photos.service.RecommendedPhotos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchDashboardViewModel @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val recommendationService: RecommendationService,
    private val featureFlagProvider: FeatureFlagProvider
) : ViewModel() {

    private val recommendedPhotosLoadedMutableEvent= MutableLiveData<RecommendedPhotos>()
    val recommendedPhotosLoadedEvent: LiveData<RecommendedPhotos> = recommendedPhotosLoadedMutableEvent

    init {
        loadRecommendations(reset = true)
    }

    fun loadRecommendations(reset: Boolean = false) {
        if (!featureFlagProvider.isFeatureEnabled(FeatureFlag.RECOMMENDATIONS)) {
            return
        }

        if (reset) {
            recommendationService.reset()
        }

        viewModelScope.launch {
            val recommendation = recommendationService.getNextRecommendation()

            if (recommendation.query != null) {
                val photos = photosRepository.searchPhotos(recommendation.query,
                        ListConfig.RECOMMENDATIONS_LIMIT)

                recommendedPhotosLoadedMutableEvent.postValue(
                    RecommendedPhotos(photos,
                        recommendation.moreAvailable, reset)
                )
            } else {
                recommendedPhotosLoadedMutableEvent.postValue(
                    RecommendedPhotos(emptyList(),
                        recommendation.moreAvailable, reset)
                )
            }
        }
    }
}
