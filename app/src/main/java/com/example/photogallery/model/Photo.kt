package com.example.photogallery.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Photo(
    val id: String,
    val title: String,
    @Json(name = "url_s") val urlSmall: String
)