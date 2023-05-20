package com.example.photogallery.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Photos(
    val page: Int,
    val pages: Int,
    @Json(name = "perpage") val photosPerPage: Int,
    val total: Int,
    @Json(name = "photo") val photos: List<Photo>
)
