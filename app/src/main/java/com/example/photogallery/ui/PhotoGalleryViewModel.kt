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
                initializeUiState()
            } catch (exception: Exception) {
                Log.d(TAG, exception.localizedMessage ?: "Unknown exception")
            }
        }
    }

    private suspend fun initializeUiState() {
        val searchText = preferencesRepository.searchTextPreference.first()
        Log.d(TAG, "Initialize with search text $searchText")
        val photos = initializePhotosWithSearchText(searchText)
        Log.d(TAG, "Initialize with photos $photos")
        val isPolling = preferencesRepository.isPollingPreference.first()
        Log.d(TAG, "Initialize with is polling $isPolling")
        _uiState.update { oldState ->
            oldState.copy(
                photos = photos,
                searchText = searchText,
                isPolling = isPolling
            )
        }
    }

    private suspend fun initializePhotosWithSearchText(searchText: String): List<Photo> {
        return if (searchText.isNotEmpty()) {
            photoRepository.getPhotosBySearchText(searchText)
        } else {
            photoRepository.getInterestingnessPhotos()
        }
    }

    private suspend fun getPhotosWithSearchText(searchText: String) {
        val photos = if (searchText.isNotEmpty()) {
            photoRepository.getPhotosBySearchText(searchText)
        } else {
            photoRepository.getInterestingnessPhotos()
        }
        Log.d(TAG, "Get new photos: $photos")
        _uiState.update { it.copy(photos = photos) }
    }

    fun searchFor(searchText: String) {
        viewModelScope.launch {
            preferencesRepository.setSearchTextPreferenceTo(searchText)
            Log.d(TAG, "Search text preference set to $searchText")
            _uiState.update { it.copy(searchText = searchText) }
            getPhotosWithSearchText(searchText)
        }
    }

    fun setIsPollingTo(isPolling: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setIsPollingPreferenceTo(isPolling)
            Log.d(TAG, "Is polling preference set to $isPolling")
            _uiState.update { it.copy(isPolling = isPolling) }
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