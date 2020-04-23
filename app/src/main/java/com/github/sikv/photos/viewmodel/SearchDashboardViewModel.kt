package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sikv.photos.App
import com.github.sikv.photos.RuntimeBehaviour
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.config.Config
import com.github.sikv.photos.data.repository.SearchTagRepository
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.SearchTag
import com.github.sikv.photos.recommendation.Recommender
import com.github.sikv.photos.util.subscribeAsync
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class SearchDashboardViewModel : ViewModel() {

    companion object {
        private const val RECOMMENDATIONS_LIMIT = 18
    }

    @Inject
    lateinit var searchTagRepository: SearchTagRepository

    @Inject
    lateinit var recommender: Recommender

    private val searchTagsMutableLiveData = MutableLiveData<List<SearchTag>>()
    val searchTagsLiveData: LiveData<List<SearchTag>> = searchTagsMutableLiveData

    private val recommendedPhotosLoadedMutableEvent= MutableLiveData<Event<Pair<List<Photo>, Boolean>>>()
    /**
     * Returns a list of recommended photos and a flag if more are available.
     */
    val recommendedPhotosLoadedEvent: LiveData<Event<Pair<List<Photo>, Boolean>>> = recommendedPhotosLoadedMutableEvent

    init {
        App.instance.appComponent.inject(this)

        if (RuntimeBehaviour.getConfig(Config.SEARCH_TAGS_ENABLED)) {
            loadSearchTags()
        }
    }

    fun loadRecommendations() {
        recommender.reset()

        viewModelScope.launch {
            loadRecommendationsAsync()
        }
    }

    fun loadMoreRecommendations() {
        viewModelScope.launch {
            loadRecommendationsAsync()
        }
    }

    private fun loadSearchTags() {
        val language = Locale.getDefault().language

        searchTagRepository.getTags(language) { searchTags ->
            searchTagsMutableLiveData.postValue(searchTags)
        }
    }

    private suspend fun loadRecommendationsAsync() {
        val recommendation = recommender.getNextRecommendation()

        val query = recommendation.first
        val moreAvailable = recommendation.second

        if (query != null) {
            ApiClient.INSTANCE.searchPhotos(query, RECOMMENDATIONS_LIMIT)
                    .subscribeAsync({
                        recommendedPhotosLoadedMutableEvent.postValue(Event(Pair(it, moreAvailable)))
                    }, {
                        recommendedPhotosLoadedMutableEvent.postValue(Event(Pair(emptyList(), false)))
                    })
        } else {
            recommendedPhotosLoadedMutableEvent.postValue(Event(Pair(emptyList(), moreAvailable)))
        }
    }
}