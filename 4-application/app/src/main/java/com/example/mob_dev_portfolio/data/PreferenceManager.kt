package com.example.mob_dev_portfolio.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manager for app preferences using DataStore.
 * Replacing SharedPreferences as per MAD guidelines and assignment requirements.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    private val LAST_UPDATED_KEY = longPreferencesKey("last_updated")
    private val THEME_KEY = intPreferencesKey("theme_selection")

    /**
     * Theme selection: 0 = Auto/System, 1 = Light, 2 = Dark, 3 = Noctua Brown
     */
    val themeSelection: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: 0
        }

    suspend fun saveThemeSelection(theme: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    /**
     * Get the last updated timestamp.
     */
    val lastUpdated: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_UPDATED_KEY] ?: 0L
        }

    /**
     * Save the last updated timestamp.
     */
    suspend fun saveLastUpdated(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_UPDATED_KEY] = timestamp
        }
    }
}
