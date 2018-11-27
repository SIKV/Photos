package com.github.sikv.photos.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "PhotoData")
data class PhotoData(
        @PrimaryKey
        var id: String,

        var url: String
) {

    constructor(): this("", "")
}