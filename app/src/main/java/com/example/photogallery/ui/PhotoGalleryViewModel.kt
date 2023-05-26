package com.example.photogallery.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogallery.PhotoRepository
import com.example.photogallery.model.Photo
import com.example.photogallery.preferences.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotoGalleryViewModel : ViewModel() {

    private val photoRepository: PhotoRepository = PhotoRepository()
    private val preferencesRepository = PreferencesRepository.getInstance()

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
        get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.storedSearchText.collectLatest { searchText ->
                try {
                    val photos = getPhotosWithSearchText(searchText)
                    Log.d(TAG, photos.toString())
                    _uiState.update { oldState ->
                        oldState.copy(
                            photos = photos,
                            searchText = searchText
                        )
                    }
                } catch (exception: Exception) {
                    Log.d(TAG, exception.localizedMessage ?: "Unknown exception")
                }
            }
        }
    }

    private suspend fun getPhotosWithSearchText(text: String): List<Photo> {
        return if (text.isNotEmpty()) {
            photoRepository.getPhotosBySearchText(text)
        } else {
            photoRepository.getInterestingnessPhotos()
        }

    }

    fun searchFor(text: String) {
        viewModelScope.launch {
            preferencesRepository.setSearchQueryPreferenceWith(text)
        }
    }

    companion object {

        private const val TAG = "PhotoGalleryFragment"
    }

    data class UiState(
        val photos: List<Photo> = listOf(),
        val searchText: String = ""
    )

}