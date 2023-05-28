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
import kotlinx.coroutines.flow.first
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
            try {
                updateUiState()
            } catch (exception: Exception) {
                Log.d(TAG, exception.localizedMessage ?: "Unknown exception")
            }
        }
    }

    private suspend fun updateUiState() {
        val searchText = preferencesRepository.searchTextPreference.first()
        //TODO everytime we update UI State we are calling Flickr API... BAD!
        val photos = getPhotosWithSearchText(searchText)
        val isPolling = preferencesRepository.isPollingPreference.first()
        Log.d(TAG, photos.toString())
        _uiState.update { oldState ->
            oldState.copy(
                photos = photos,
                searchText = searchText,
                isPolling = isPolling
            )
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
            preferencesRepository.setSearchTextPreferenceTo(text)
            Log.d(TAG, "Search text preference set to $text")
            updateUiState()
        }
    }

    fun setIsPollingTo(isPolling: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setIsPollingPreferenceTo(isPolling)
            Log.d(TAG, "Is polling preference set to $isPolling")
            updateUiState()
        }
    }

    companion object {

        private const val TAG = "PhotoGalleryFragment"
    }

    data class UiState(
        val photos: List<Photo> = listOf(),
        val searchText: String = "",
        val isPolling: Boolean = false
    )

}