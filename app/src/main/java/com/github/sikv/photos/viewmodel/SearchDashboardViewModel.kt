package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sikv.photos.App
import com.github.sikv.photos.RuntimeBehaviour
import com.github.sikv.photos.config.Config
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.recommendation.RecommendedPhotos
import com.github.sikv.photos.recommendation.Recommender
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchDashboardViewModel : ViewModel() {

    companion object {
        private const val RECOMMENDATIONS_LIMIT = 18
    }

    @Inject
    lateinit var photosRepository: PhotosRepository

    @Inject
    lateinit var recommender: Recommender

    private val recommendedPhotosLoadedMutableEvent= MutableLiveData<RecommendedPhotos>()
    val recommendedPhotosLoadedEvent: LiveData<RecommendedPhotos> = recommendedPhotosLoadedMutableEvent

    init {
        App.instance.appComponent.inject(this)

        loadRecommendations(reset = true)
    }

    fun loadRecommendations(reset: Boolean = false) {
        if (!RuntimeBehaviour.getConfig(Config.RECOMMENDATIONS_ENABLED)) {
            return
        }

        if (reset) {
            recommender.reset()
        }

        viewModelScope.launch {
            val recommendation = recommender.getNextRecommendation()

            if (recommendation.query != null) {
                val photos = photosRepository.searchPhotos(recommendation.query, RECOMMENDATIONS_LIMIT)

                recommendedPhotosLoadedMutableEvent.postValue(RecommendedPhotos(photos, recommendation.moreAvailable, reset))
            } else {
                recommendedPhotosLoadedMutableEvent.postValue(RecommendedPhotos(emptyList(), recommendation.moreAvailable, reset))
            }
        }
    }
}