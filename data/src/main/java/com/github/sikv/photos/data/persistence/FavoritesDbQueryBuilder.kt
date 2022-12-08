package com.github.sikv.photos.data.persistence

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.github.sikv.photos.data.SortBy
import javax.inject.Inject

class FavoritesDbQueryBuilder @Inject constructor() {

    fun buildGetPhotosQuery(sortBy: SortBy): SupportSQLiteQuery {
        val orderBy = when (sortBy) {
            SortBy.DATE_ADDED_NEWEST -> "dateAdded DESC"
            SortBy.DATE_ADDED_OLDEST -> "dateAdded ASC"
        }
        return SimpleSQLiteQuery("SELECT * from ${DbConfig.favoritePhotosTableName} WHERE markedAsDeleted=0 ORDER BY $orderBy")
    }
}
