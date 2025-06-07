package com.example.dimoraapp.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dimoraapp.data.model.ProfileState
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        val USER_PROFILE_KEY = stringPreferencesKey("user_profile")
    }

    private val gson = Gson()

    // Read profile as Flow<ProfileState?>
    val userProfileFlow: Flow<ProfileState?> = context.dataStore.data
        .map { prefs ->
            prefs[USER_PROFILE_KEY]?.let { json ->
                try {
                    gson.fromJson(json, ProfileState::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        }

    // Save profile as JSON string
    suspend fun saveUserProfile(profile: ProfileState) {
        val json = gson.toJson(profile)
        context.dataStore.edit { prefs ->
            prefs[USER_PROFILE_KEY] = json
        }
    }
}