package com.github.sikv.photos.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.sikv.photos.config.DbConfig

@Entity(tableName = DbConfig.remotePagesTableName)
data class RemotePageEntity(
        @PrimaryKey
        val label: String,

        val nextPage: Int?
)