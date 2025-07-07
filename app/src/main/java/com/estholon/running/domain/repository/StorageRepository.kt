package com.estholon.running.domain.repository

import android.net.Uri

interface StorageRepository {

    fun uploadImage(runId: String, uri: Uri)
    suspend fun downloadImages(runId: String) : List<Uri>

}