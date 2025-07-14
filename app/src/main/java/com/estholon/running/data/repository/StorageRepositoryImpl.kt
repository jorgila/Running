package com.estholon.running.data.repository

import android.net.Uri
import com.estholon.running.data.datasource.StorageDataSource
import com.estholon.running.domain.repository.StorageRepository
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storageDataSource: StorageDataSource
): StorageRepository {

    override fun uploadImage(runId: String, uri: Uri) {
        storageDataSource.uploadImage(runId,uri)
    }

    override fun uploadVideo(runId: String, uri: Uri) {
        storageDataSource.uploadVideo(runId,uri)
    }

    override suspend fun downloadImages(runId: String): List<Uri> {
        return storageDataSource.downloadImages(runId)
    }

    override suspend fun downloadVideos(runId: String): List<Uri> {
        return storageDataSource.downloadVideos(runId)
    }

    override suspend fun deleteImages(runId: String) : Boolean {
        return storageDataSource.deleteImages(runId)
    }

    override suspend fun deleteVideos(runId: String) : Boolean {
        return storageDataSource.deleteVideos(runId)
    }

}