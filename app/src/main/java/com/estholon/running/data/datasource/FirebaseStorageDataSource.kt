package com.estholon.running.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class FirebaseStorageDataSource @Inject constructor(
    private val storage: FirebaseStorage
) : StorageDataSource {

    override fun uploadImage(runId: String, uri: Uri) {

        val name = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
            .format(System.currentTimeMillis())

        val reference = storage.reference
            .child("images/")
            .child("$runId/")
            .child("$name.jpg")
        reference.putFile(uri)
    }

    override suspend fun downloadImages(runId: String): List<Uri> {
        return listOf(Uri.EMPTY,Uri.EMPTY)
    }
}