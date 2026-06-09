package com.ishan.kbc.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kbc_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USERNAME = stringPreferencesKey("username")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val COINS = stringPreferencesKey("coins")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { it[Keys.ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[Keys.REFRESH_TOKEN] }
    val username: Flow<String?> = context.dataStore.data.map { it[Keys.USERNAME] }
    val displayName: Flow<String?> = context.dataStore.data.map { it[Keys.DISPLAY_NAME] }
    val coins: Flow<Int> = context.dataStore.data.map { it[Keys.COINS]?.toIntOrNull() ?: 0 }

    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.SOUND_ENABLED] ?: true }
    val musicEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.MUSIC_ENABLED] ?: true }
    val hapticsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.HAPTICS_ENABLED] ?: true }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }

    suspend fun saveSession(accessToken: String, refreshToken: String, username: String, displayName: String?) {
        context.dataStore.edit { p ->
            p[Keys.ACCESS_TOKEN] = accessToken
            p[Keys.REFRESH_TOKEN] = refreshToken
            p[Keys.USERNAME] = username
            if (displayName != null) p[Keys.DISPLAY_NAME] = displayName
        }
    }

    suspend fun setCoins(value: Int) {
        context.dataStore.edit { it[Keys.COINS] = value.toString() }
    }

    suspend fun setSoundEnabled(v: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_ENABLED] = v }
    }

    suspend fun setMusicEnabled(v: Boolean) {
        context.dataStore.edit { it[Keys.MUSIC_ENABLED] = v }
    }

    suspend fun setHapticsEnabled(v: Boolean) {
        context.dataStore.edit { it[Keys.HAPTICS_ENABLED] = v }
    }

    suspend fun setNotificationsEnabled(v: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = v }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
