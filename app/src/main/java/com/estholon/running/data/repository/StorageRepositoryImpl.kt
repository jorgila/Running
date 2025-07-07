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

    override suspend fun downloadImages(runId: String): List<Uri> {
        return storageDataSource.downloadImages(runId)
    }

}