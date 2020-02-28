package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sikv.photos.App
import com.github.sikv.photos.data.repository.SearchTagsRepository
import com.github.sikv.photos.model.SearchTag
import java.util.*
import javax.inject.Inject

class SearchDashboardViewModel : ViewModel() {

    @Inject
    lateinit var searchTagsRepository: SearchTagsRepository

    private val searchTagsMutableLiveData = MutableLiveData<List<SearchTag>>()
    val searchTagsLiveData: LiveData<List<SearchTag>> = searchTagsMutableLiveData

    init {
        App.instance.appComponent.inject(this)

        val language = Locale.getDefault().language

        searchTagsRepository.getTags(language) { searchTags ->
            searchTagsMutableLiveData.postValue(searchTags)
        }
    }
}