package com.example.photogallery

import com.example.photogallery.model.Photo
import com.example.photogallery.network.FlickrApi

class PhotoRepository {

    private val flickrApi: FlickrApi = FlickrApi.getInstance()

    suspend fun getInterestingnessPhotos(): List<Photo> {
        val flickrResponse = flickrApi.getInterestingness()
        return flickrResponse.photos.photos
    }

    suspend fun getPhotosBySearchText(text: String): List<Photo> {
        val flickrResponse = flickrApi.getPhotosBySearchText(text)
        return flickrResponse.photos.photos
    }
}