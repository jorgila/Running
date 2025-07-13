package com.estholon.running.domain.repository

import android.net.Uri

interface StorageRepository {

    fun uploadImage(runId: String, uri: Uri)
    fun uploadVideo(runId: String, uri: Uri)
    suspend fun downloadImages(runId: String) : List<Uri>
    suspend fun downloadVideos(runId: String) : List<Uri>

}