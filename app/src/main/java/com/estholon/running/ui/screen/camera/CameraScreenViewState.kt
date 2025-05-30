package com.estholon.running.ui.screen.camera

import android.net.Uri
import androidx.camera.core.CameraSelector

sealed class CameraScreenViewState {

    data class CameraUIState(
        val isInitialized: Boolean = false,
        val isRecording: Boolean = false,
        val isPaused: Boolean = false,
        val isFlashEnabled: Boolean = false,
        val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
        val error: String? = null,
        val lastCapturedPhotoUri: Uri? = null,
        val lastRecordedVideoUri: Uri? = null,
        val recordingDuration: Long = 0L
    )

}