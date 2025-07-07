package com.estholon.running.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import javax.inject.Inject

class FirebaseStorageDataSource @Inject constructor(
    private val storage: FirebaseStorage
) : StorageDataSource {

    override fun uploadImage(runId: String, uri: Uri) {
        val reference = storage.reference
            .child("images/")
            .child("$runId/")
            .child(uri.lastPathSegment.orEmpty())
        reference.putFile(uri)
    }

    override suspend fun downloadImages(runId: String): List<Uri> {
        return listOf(Uri.EMPTY,Uri.EMPTY)
    }
}