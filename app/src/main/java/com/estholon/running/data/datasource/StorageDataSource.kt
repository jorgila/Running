package com.estholon.running.data.datasource

import android.net.Uri
import java.io.File

interface StorageDataSource {

    fun uploadImage(runId: String, uri: Uri)
    fun uploadVideo(runId: String, uri: Uri)
    suspend fun downloadImages(runId: String) : List<Uri>
    suspend fun downloadVideos(runId: String) : List<Uri>

}