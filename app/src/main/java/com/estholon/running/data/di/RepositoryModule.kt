package com.estholon.running.data.di

import com.estholon.running.data.datasource.FirestoreDataSource
import com.estholon.running.data.datasource.DatabaseDataSource
import com.estholon.running.data.mapper.LevelMapper
import com.estholon.running.data.mapper.LocationMapper
import com.estholon.running.data.mapper.RunMapper
import com.estholon.running.data.mapper.TotalMapper
import com.estholon.running.data.repository.RunningRepositoryImpl
import com.estholon.running.domain.repository.RunningRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRunningRepository(
        runningRepositoryImpl: RunningRepositoryImpl
    ): RunningRepository

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(
        firestoreDataSource: FirestoreDataSource
    ): DatabaseDataSource

    companion object {
        @Provides
        @Singleton
        fun provideRunMapper(): RunMapper = RunMapper()

        @Provides
        @Singleton
        fun provideTotalMapper(): TotalMapper = TotalMapper()

        @Provides
        @Singleton
        fun provideLevelMapper(): LevelMapper = LevelMapper()

        @Provides
        @Singleton
        fun provideLocationMapper(): LocationMapper = LocationMapper()
    }
}