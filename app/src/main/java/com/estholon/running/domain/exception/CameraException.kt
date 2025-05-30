package com.estholon.running.domain.exception

sealed class CameraException(message: String) : Exception( message ){

    class InitializationFailed(message: String) : CameraException("Camera initialization failed: $message")
    class PhotoCaptureFailed(message: String) : CameraException("Photo capture failed: $message")
    class VideoRecordingFailed(message: String) : CameraException("Video recording failed: $message")
    class CameraSwitchFailed(message: String): CameraException("Camera switch failed: $message")
    class FlashToggleFailed(message: String) : CameraException("Flash toggle failed: $message")
    class PermissionDenied(message: String) : CameraException("Permission denied: $message")

}