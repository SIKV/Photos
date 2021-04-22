package com.github.sikv.photos.data.mediator

import android.content.SharedPreferences
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.database.CuratedDb
import com.github.sikv.photos.database.entity.CuratedPhotoEntity
import com.github.sikv.photos.database.entity.RemotePageEntity
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class CuratedPhotosRemoteMediator(
        private val db: CuratedDb,
        private val photosRepository: PhotosRepository,
        private val preferences: SharedPreferences
) : RemoteMediator<Int, CuratedPhotoEntity>() {

    private val dbLastUpdatedKey = "dbLastUpdated"
    private val remotePageQuery = "Curated"

    private val cacheTimeoutHours = 12L

    override suspend fun initialize(): InitializeAction {
        val dbLastUpdated = preferences.getLong(dbLastUpdatedKey, 0)

        if (dbLastUpdated <= 0) {
            return InitializeAction.LAUNCH_INITIAL_REFRESH
        }

        val cacheTimeout = TimeUnit.HOURS.toMillis(cacheTimeoutHours)

        return if (System.currentTimeMillis() - dbLastUpdated >= cacheTimeout) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, CuratedPhotoEntity>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remotePage = db.withTransaction {
                        db.remotePageDao.remotePageByQuery(remotePageQuery)
                    }

                    if (remotePage?.nextPage == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    remotePage.nextPage
                }
            }

            val photos = photosRepository.getCuratedPhotos(
                    page = page,
                    perPage = state.config.initialLoadSize
            )

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remotePageDao.deleteByQuery(remotePageQuery)
                    db.curatedDao.clearAll()
                }

                db.remotePageDao.insertOrReplace(
                        RemotePageEntity(remotePageQuery, page + 1)
                )

                db.curatedDao.insertAll(photos.map { CuratedPhotoEntity.fromPhoto(it) })

                preferences.edit()
                        .putLong(dbLastUpdatedKey, System.currentTimeMillis())
                        .commit()
            }

            return MediatorResult.Success(endOfPaginationReached = photos.isEmpty())

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}