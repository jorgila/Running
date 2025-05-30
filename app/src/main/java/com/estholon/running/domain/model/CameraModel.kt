package com.estholon.running.domain.model

data class CameraModel(
    val isInitialized: Boolean = false,
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val isFlashEnabled: Boolean = false,
    val lensFacing: CameraLensFacing = CameraLensFacing.BACK,
    val error: String? = null,
    val lastCapturedPhotoUri: String? = null,
    val lastRecordedVideoUri: String? = null,
    val recordingDuration: Long = 0L
)

enum class CameraLensFacing {
    FRONT, BACK
}
