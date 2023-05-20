package com.example.photogallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogallery.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel : ViewModel() {

    private val repository: PhotoRepository = PhotoRepository()

    private val _photos: MutableStateFlow<List<Photo>> = MutableStateFlow(emptyList())
    val photos: StateFlow<List<Photo>>
        get() = _photos.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val photos = repository.getInterestingnessPhotos()
                Log.d("test", photos.toString())
                _photos.value = photos
            } catch (exception: Exception) {
                Log.d("test", exception.localizedMessage ?: "Unknown exception")
            }
        }
    }

}