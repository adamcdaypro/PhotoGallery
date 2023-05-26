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

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {

    val storedSearchText: Flow<String> = dataStore.data.map {
        it[SEARCH_TEXT_KEY] ?: ""
    }.distinctUntilChanged()

    suspend fun setSearchQueryPreferenceWith(query: String) {
        dataStore.edit { it[SEARCH_TEXT_KEY] = query }
    }

    companion object {

        private const val TAG = "PreferencesRepository"
        private const val PREFERENCES_FILE_NAME = "preferences"

        private val SEARCH_TEXT_KEY = stringPreferencesKey("search_text")

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