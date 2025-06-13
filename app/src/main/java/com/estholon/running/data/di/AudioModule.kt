package com.estholon.running.data.di

import android.content.Context
import com.estholon.running.data.datasource.AudioDataSource
import com.estholon.running.data.datasource.MediaPlayerDataSource
import com.estholon.running.data.repository.AudioRepositoryImpl
import com.estholon.running.domain.repository.AudioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {

    @Provides
    @Singleton
    fun provideAudioDataSource(
        @ApplicationContext context: Context
    ): AudioDataSource = MediaPlayerDataSource(context)

    @Provides
    @Singleton
    fun provideAudioRepository(
        audioDataSource: AudioDataSource
    ): AudioRepository = AudioRepositoryImpl(audioDataSource)
}