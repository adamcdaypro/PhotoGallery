package com.example.photogallery

import java.util.UUID

data class Photo(
    val id: UUID,
    val owner: String = "",
    val title: String = "",
    val url_small: String = "",
)