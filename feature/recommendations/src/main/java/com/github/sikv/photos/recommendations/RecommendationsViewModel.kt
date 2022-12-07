package com.github.sikv.photos.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.recommendations.service.RecommendationsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecommendationsUiState(
    val photos: List<Photo>,
    val isLoading: Boolean,
    val isNextPageLoading: Boolean
)

@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val recommendationService: RecommendationsService,
    private val configProvider: ConfigProvider
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(
        RecommendationsUiState(
            photos = emptyList(),
            isLoading = false,
            isNextPageLoading = false
        )
    )
    val uiState: StateFlow<RecommendationsUiState> = mutableUiState

    init {
        loadRecommendations(true)
    }

    fun loadRecommendations(refresh: Boolean = false) {
        viewModelScope.launch {
            setLoadingState(refresh)

            val recommendation = recommendationService.getNextRecommendation()

            if (recommendation.query != null) {
                val photos = searchPhotosWithRandomSearchSource(
                    query = recommendation.query,
                    limit = configProvider.getRecommendationsLimit()
                )
                appendPhotos(photos, refresh)
            } else {
                appendPhotos(emptyList(), refresh)
            }
        }
    }

    private suspend fun searchPhotosWithRandomSearchSource(query: String, limit: Int): List<Photo> {
        val searchSources = configProvider.getSearchSources()

        return if (searchSources.isEmpty()) {
            emptyList()
        } else {
            photosRepository
                .searchPhotos(query, 0, limit, searchSources.random())
                .shuffled()
        }
    }

    private fun setLoadingState(refresh: Boolean) {
        mutableUiState.update { currentState ->
            if (refresh) {
                currentState.copy(
                    isLoading = true,
                    isNextPageLoading = false
                )
            } else {
                currentState.copy(
                    isLoading = false,
                    isNextPageLoading = true
                )
            }
        }
    }

    private fun appendPhotos(photos: List<Photo>, refresh: Boolean) {
        mutableUiState.update { currentState ->
            val currentPhotos = if (refresh) emptyList() else currentState.photos

            RecommendationsUiState(
                photos = currentPhotos + photos,
                isLoading = false,
                isNextPageLoading = false,
            )
        }
    }
}
