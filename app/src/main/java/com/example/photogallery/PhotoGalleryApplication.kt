package com.example.photogallery

import android.app.Application
import com.example.photogallery.preferences.PreferencesRepository

class PhotoGalleryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
    }
}