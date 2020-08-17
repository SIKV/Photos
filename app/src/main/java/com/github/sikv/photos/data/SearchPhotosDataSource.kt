package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.github.sikv.photos.App
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.enumeration.DataSourceState
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchPhotosDataSource(
        private val photoSource: PhotoSource,
        private val searchQuery: String
) : PositionalDataSource<Photo>() {

    @Inject
    lateinit var photosRepository: PhotosRepository

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    var state: MutableLiveData<DataSourceState> = MutableLiveData()
        private set

    init {
        App.instance.appComponent.inject(this)
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        updateState(DataSourceState.LOADING_INITIAL)

        val page = params.requestedStartPosition
        val perPage = params.requestedLoadSize

        searchPhotos(page, perPage) {
            callback.onResult(it, 0, it.size)
            updateState(DataSourceState.INITIAL_LOADING_DONE)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
        updateState(DataSourceState.LOADING_NEXT)

        val page = params.startPosition
        val perPage = params.loadSize

        searchPhotos(page, perPage) {
            callback.onResult(it)
            updateState(DataSourceState.NEXT_DONE)
        }
    }

    private fun searchPhotos(page: Int, perPage: Int, completion: suspend (List<Photo>) -> Unit): Job {
        return scope.launch {
            try {
                val photos = photosRepository.searchPhotos(searchQuery, page, perPage, photoSource)
                completion(photos)
            } catch (e: Exception) {
                updateState(DataSourceState.ERROR)
            }
        }
    }

    private fun updateState(state: DataSourceState) {
        this.state.postValue(state)
    }
}