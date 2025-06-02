package com.estholon.running.ui.screen.camera

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.data.repository.CameraRepositoryImpl
import com.estholon.running.domain.model.CameraModel
import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.camera.CapturePhotoUseCase
import com.estholon.running.domain.useCase.camera.ClearErrorUseCase
import com.estholon.running.domain.useCase.camera.InitializeCameraUseCase
import com.estholon.running.domain.useCase.camera.PauseVideoRecordingUseCase
import com.estholon.running.domain.useCase.camera.ResumeVideoRecordingUseCase
import com.estholon.running.domain.useCase.camera.StartVideoRecordingUseCase
import com.estholon.running.domain.useCase.camera.StopVideoRecordingUseCase
import com.estholon.running.domain.useCase.camera.SwitchCameraUseCase
import com.estholon.running.domain.useCase.camera.ToggleFlashUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val initializeCameraUseCase: InitializeCameraUseCase,
    private val capturePhotoUseCase: CapturePhotoUseCase,
    private val startVideoRecordingUseCase: StartVideoRecordingUseCase,
    private val stopVideoRecordingUseCase: StopVideoRecordingUseCase,
    private val pauseRecordingUseCase: PauseVideoRecordingUseCase,
    private val resumeRecordingUseCase: ResumeVideoRecordingUseCase,
    private val switchCameraUseCase: SwitchCameraUseCase,
    private val toggleFlashUseCase: ToggleFlashUseCase,
    private val clearErrorUseCase: ClearErrorUseCase,
    private val cameraRepository: CameraRepository
) : ViewModel() {
    
    val uiState: StateFlow<CameraModel> = cameraRepository.cameraState

    fun initializeCamera(surfaceProvider: Any, lifecycleOwner: Any) {
        viewModelScope.launch {
            initializeCameraUseCase(surfaceProvider, lifecycleOwner)
                .onFailure { exception ->
                    // Handle error - already handled in use case and repository
                    android.util.Log.e("CameraViewModel", "Failed to initialize camera", exception)
                }
        }
    }

    fun capturePhoto() {
        viewModelScope.launch {
            capturePhotoUseCase()
                .onSuccess { uri ->
                    android.util.Log.d("CameraViewModel", "Photo captured: $uri")
                }
                .onFailure { exception ->
                    android.util.Log.e("CameraViewModel", "Failed to capture photo", exception)
                }
        }
    }

    fun startRecording() {
        viewModelScope.launch {
            startVideoRecordingUseCase()
                .onFailure { exception ->
                    android.util.Log.e("CameraViewModel", "Failed to start recording", exception)
                }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            stopVideoRecordingUseCase()
                .onSuccess { uri ->
                    android.util.Log.d("CameraViewModel", "Recording stopped: $uri")
                }
                .onFailure { exception ->
                    android.util.Log.e("CameraViewModel", "Failed to stop recording", exception)
                }
        }
    }

    fun pauseRecording() {
        viewModelScope.launch {
            pauseRecordingUseCase()
        }
    }

    fun resumeRecording() {
        viewModelScope.launch {
            resumeRecordingUseCase()
        }
    }

    fun switchCamera(surfaceProvider: Any, lifecycleOwner: Any) {
        viewModelScope.launch {
            switchCameraUseCase(surfaceProvider, lifecycleOwner)
        }
    }

    fun toggleFlash() {
        viewModelScope.launch {
            toggleFlashUseCase()
        }
    }

    fun clearError() {
        clearErrorUseCase()
    }

    override fun onCleared() {
        super.onCleared()
        cameraRepository.cleanup()
    }



}