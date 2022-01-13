package com.github.sikv.photos.data.storage

import androidx.room.TypeConverter
import com.github.sikv.photos.enumeration.PhotoSource

object Converters {

    @TypeConverter
    @JvmStatic
    fun toPhotoSource(value: String) = enumValueOf<PhotoSource>(value)

    @TypeConverter
    @JvmStatic
    fun fromPhotoSource(value: PhotoSource) = value.name
}