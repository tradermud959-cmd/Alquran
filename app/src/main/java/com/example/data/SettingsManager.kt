package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val TARGET_TADARUS_TYPE = stringPreferencesKey("target_tadarus_type")
        val TARGET_TADARUS_VALUE = intPreferencesKey("target_tadarus_value")
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: "Hamba Allah"
    }

    val targetTadarusTypeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TARGET_TADARUS_TYPE] ?: "Halaman"
    }

    val targetTadarusValueFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TARGET_TADARUS_VALUE] ?: 10
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun setTargetTadarus(type: String, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[TARGET_TADARUS_TYPE] = type
            preferences[TARGET_TADARUS_VALUE] = value
        }
    }
}
