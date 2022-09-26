package com.github.sikv.photos.persistence

import androidx.room.TypeConverter
import com.github.sikv.photos.domain.PhotoSource

object Converters {

    @TypeConverter
    @JvmStatic
    fun toPhotoSource(value: String) = enumValueOf<PhotoSource>(value)

    @TypeConverter
    @JvmStatic
    fun fromPhotoSource(value: PhotoSource) = value.name
}
