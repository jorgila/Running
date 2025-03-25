package com.estholon.running.data.di

import android.content.Context
import android.content.SharedPreferences
import com.estholon.running.data.manager.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferencesModule {

    companion object {
        private const val PREFERENCES_NAME = "shared_preferences"
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(sharedPreferences: SharedPreferences): PreferencesManager {
        PreferencesManager.sharedPreferences = sharedPreferences
        PreferencesManager.sharedPreferencesEditor = sharedPreferences.edit()
        return PreferencesManager
    }



}