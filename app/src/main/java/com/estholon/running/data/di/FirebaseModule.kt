package com.estholon.running.data.di

import com.estholon.running.data.datasource.AnalyticsDataSource
import com.estholon.running.data.datasource.AuthenticationDataSource
import com.estholon.running.data.datasource.DatabaseDataSource
import com.estholon.running.data.datasource.FirebaseAnalyticsDataSource
import com.estholon.running.data.datasource.FirebaseAuthDataSource
import com.estholon.running.data.datasource.FirebaseFirestoreDataSource
import com.estholon.running.data.mapper.AnalyticsMapper
import com.estholon.running.data.mapper.UserMapper
import com.estholon.running.data.repository.AnalyticsRepositoryImpl
import com.estholon.running.data.repository.AuthenticationRepositoryImpl
import com.estholon.running.domain.repository.AnalyticsRepository
import com.estholon.running.domain.repository.AuthenticationRepository
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(
        firebaseFirestoreDataSource: FirebaseFirestoreDataSource
    ): DatabaseDataSource

    @Binds
    @Singleton
    abstract fun bindAuthenticationDataSource(
        firebaseAuthDataSource: FirebaseAuthDataSource
    ): AuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindAuthenticationRepository(
        authenticationRepositoryImpl: AuthenticationRepositoryImpl
    ): AuthenticationRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsDataSource(
        firebaseAnalyticsDataSource: FirebaseAnalyticsDataSource
    ): AnalyticsDataSource

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        analyticsRepositoryImpl: AnalyticsRepositoryImpl
    ): AnalyticsRepository

    companion object {

        @Singleton
        @Provides
        fun provideFirebaseAuth() = FirebaseAuth.getInstance()

        @Singleton
        @Provides
        fun provideFirebaseAnalytics() = Firebase.analytics

        @Singleton
        @Provides
        fun provideFirebaseFirestore() = Firebase.firestore

        @Provides
        @Singleton
        fun provideUserMapper(): UserMapper = UserMapper()

        @Provides
        @Singleton
        fun provideAnalyticsMapper() : AnalyticsMapper = AnalyticsMapper()

    }

}