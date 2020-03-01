package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sikv.photos.App
import com.github.sikv.photos.data.repository.SearchTagRepository
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.SearchTag
import java.util.*
import javax.inject.Inject

class SearchDashboardViewModel : ViewModel() {

    @Inject
    lateinit var searchTagRepository: SearchTagRepository

    private val searchTagsMutableLiveData = MutableLiveData<List<SearchTag>>()
    val searchTagsLiveData: LiveData<List<SearchTag>> = searchTagsMutableLiveData

    private val suggestedPhotosMutableLiveData = MutableLiveData<List<Photo>>()
    val suggestedPhotosLiveData: LiveData<List<Photo>> = suggestedPhotosMutableLiveData

    init {
        App.instance.appComponent.inject(this)

        loadSearchTags()
        loadSuggestions()
    }

    private fun loadSearchTags() {
        val language = Locale.getDefault().language

        searchTagRepository.getTags(language) { searchTags ->
            searchTagsMutableLiveData.postValue(searchTags)
        }
    }

    private fun loadSuggestions() {
        // TODO Implement
    }
}