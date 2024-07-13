package com.github.sikv.photos.data.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.github.sikv.photos.data.PhotosDTO
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object PhotosDTOSerializer : Serializer<PhotosDTO> {

    override val defaultValue: PhotosDTO
        get() = PhotosDTO.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): PhotosDTO {
        try {
            return PhotosDTO.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: PhotosDTO, output: OutputStream) {
        t.writeTo(output)
    }
}
