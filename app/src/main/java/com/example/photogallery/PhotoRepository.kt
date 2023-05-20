package com.example.photogallery

import com.example.photogallery.model.Photo
import com.example.photogallery.network.FlickrApi

class PhotoRepository {

    private val flickrApi: FlickrApi = FlickrApi.create()

    suspend fun getInterestingnessPhotos(): List<Photo> {
        val flickrResponse = flickrApi.getInterestingness()
        return flickrResponse.photos.photos
    }
}