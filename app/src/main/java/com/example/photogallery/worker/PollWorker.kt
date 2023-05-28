package com.example.photogallery.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.photogallery.MainActivity
import com.example.photogallery.PhotoGalleryApplication
import com.example.photogallery.PhotoRepository
import com.example.photogallery.R
import com.example.photogallery.preferences.PreferencesRepository
import kotlinx.coroutines.flow.first

class PollWorker(private val context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val preferencesRepository = PreferencesRepository.getInstance()
        val searchText = preferencesRepository.searchTextPreference.first()

        if (searchText.isEmpty()) {
            Log.d(TAG, "Search text is empty, finishing early")
            return Result.success()
        }

        val photos = PhotoRepository().getPhotosBySearchText(searchText)
        if (photos.isNotEmpty()) {
            val lastPhotoId = preferencesRepository.lastPhotoIdPreference.first()
            val newPhotoId = photos.first().id
            if (lastPhotoId == newPhotoId) {
                Log.d(TAG, "The $lastPhotoId and the $newPhotoId are the same")
            } else {
                Log.d(TAG, "The $lastPhotoId and the $newPhotoId are not the same")
                preferencesRepository.setLastPhotoIdPreferenceTo(newPhotoId)
                notifyUser()
            }
        }
        return Result.success()
    }

    private fun notifyUser() {
        val intent = MainActivity.newIntent(context)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification =
            NotificationCompat.Builder(context, PhotoGalleryApplication.NOTIFICATION_CHANNEL_ID)
                .setTicker(context.getString(R.string.new_photos_notification_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(context.getString(R.string.new_photos_notification_title))
                .setContentText(context.getString(R.string.new_photos_notification_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(0, notification)
    }

    companion object {

        const val TAG = "PollWorker"

    }

}