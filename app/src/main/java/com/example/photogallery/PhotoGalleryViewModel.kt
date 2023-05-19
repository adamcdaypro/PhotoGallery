package com.example.photogallery

import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {

    private val repository: PhotoRepository = PhotoRepository()

    suspend fun getInterestingness(): String {
        return repository.getInterestingness()
    }
}