package com.github.sikv.photos.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.github.sikv.photos.database.FavoritesDatabase
import com.github.sikv.photos.database.PhotoData

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesDatabase: FavoritesDatabase

    val favoritesLiveData: LiveData<List<PhotoData>>

    init {
        favoritesDatabase = FavoritesDatabase.getInstance(getApplication())

        favoritesLiveData = favoritesDatabase.photoDao().getAll()
    }

    fun deleteAll() {
        DeleteAllAsyncTask(favoritesDatabase).execute()
    }

    private class DeleteAllAsyncTask internal constructor(
            private val db: FavoritesDatabase

    ) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            db.photoDao().deleteAll()
            return null
        }
    }
}