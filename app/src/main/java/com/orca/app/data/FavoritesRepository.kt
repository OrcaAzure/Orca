package com.orca.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "orca_prefs")

@Singleton
class FavoritesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val favoritesKey = stringSetPreferencesKey("favorite_tool_ids")

    val favorites: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[favoritesKey] ?: emptySet()
    }

    suspend fun toggle(toolId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[favoritesKey]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(toolId)) current.remove(toolId) else current.add(toolId)
            prefs[favoritesKey] = current
        }
    }

    suspend fun remove(toolId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[favoritesKey]?.toMutableSet() ?: mutableSetOf()
            current.remove(toolId)
            prefs[favoritesKey] = current
        }
    }
}
