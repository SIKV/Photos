package com.github.sikv.photos.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "PhotoData")
data class PhotoData(
        @PrimaryKey
        val id: String,

        val url: String
) {

    constructor(): this("", "")
}