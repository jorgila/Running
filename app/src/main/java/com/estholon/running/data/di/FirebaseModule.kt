package com.estholon.running.data.di

import com.estholon.running.data.datasource.DatabaseDataSource
import com.estholon.running.data.datasource.FirestoreDataSource
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
        firestoreDataSource: FirestoreDataSource
    ): DatabaseDataSource

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

    }

}