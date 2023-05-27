package com.example.photogallery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.photogallery.preferences.PreferencesRepository

class PhotoGalleryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_background_updates)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {

        const val NOTIFICATION_CHANNEL_ID = "flickr_poll"

    }
}