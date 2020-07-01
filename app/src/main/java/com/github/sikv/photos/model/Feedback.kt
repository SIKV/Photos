package com.github.sikv.photos.model

import com.google.gson.annotations.SerializedName

data class Feedback(
        @SerializedName("sessionId")
        val sessionId: String,
        
        @SerializedName("email")
        val email: String?,

        @SerializedName("description")
        val description: String
)