package com.example.photogallery.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PreferencesRepository private constructor(private val dataStore: DataStore<Preferences>) {

    val searchTextPreference: Flow<String> = dataStore.data.map {
        it[SEARCH_TEXT_KEY] ?: ""
    }

    suspend fun setSearchTextPreferenceTo(searchText: String) {
        dataStore.edit { it[SEARCH_TEXT_KEY] = searchText }
    }

    val lastPhotoIdPreference: Flow<String> = dataStore.data.map {
        it[LAST_PHOTO_ID_KEY] ?: ""
    }.distinctUntilChanged()

    suspend fun setLastPhotoIdPreferenceTo(lastPhotoId: String) {
        dataStore.edit { it[LAST_PHOTO_ID_KEY] = lastPhotoId }
    }

    companion object {

        private const val TAG = "PreferencesRepository"
        private const val PREFERENCES_FILE_NAME = "preferences"

        private val SEARCH_TEXT_KEY = stringPreferencesKey("search_text")
        private val LAST_PHOTO_ID_KEY = stringPreferencesKey("last_photo_id")

        private var INSTANCE: PreferencesRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile(PREFERENCES_FILE_NAME)
                }
                Log.d(TAG, "$TAG initialized")
                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun getInstance(): PreferencesRepository {
            return INSTANCE ?: throw IllegalStateException("$TAG must be initialized")
        }

    }

}