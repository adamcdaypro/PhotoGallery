package com.example.photogallery

import com.example.photogallery.network.FlickrApi

class PhotoRepository {

    private val flickrApi: FlickrApi = FlickrApi.create()

    suspend fun getInterestingness(): String {
        return flickrApi.getInterestingness()
    }
}