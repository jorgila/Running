package com.estholon.running.ui.screen.camera

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.domain.model.CameraModel
import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.camera.CapturePhotoResultUseCase
import com.estholon.running.domain.useCase.camera.ClearErrorResultUseCase
import com.estholon.running.domain.useCase.camera.InitializeCameraResultUseCase
import com.estholon.running.domain.useCase.camera.PauseVideoRecordingResultUseCase
import com.estholon.running.domain.useCase.camera.ResumeVideoRecordingResultUseCase
import com.estholon.running.domain.useCase.camera.StartVideoRecordingResultUseCase
import com.estholon.running.domain.useCase.camera.StopVideoRecordingResultUseCase
import com.estholon.running.domain.useCase.camera.SwitchCameraResultUseCase
import com.estholon.running.domain.useCase.camera.ToggleFlashResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val initializeCameraResultUseCase: InitializeCameraResultUseCase,
    private val capturePhotoUseCase: CapturePhotoResultUseCase,
    private val startVideoRecordingUseCase: StartVideoRecordingResultUseCase,
    private val stopVideoRecordingUseCase: StopVideoRecordingResultUseCase,
    private val pauseRecordingUseCase: PauseVideoRecordingResultUseCase,
    private val resumeRecordingUseCase: ResumeVideoRecordingResultUseCase,
    private val switchCameraResultUseCase: SwitchCameraResultUseCase,
    private val toggleFlashUseCase: ToggleFlashResultUseCase,
    private val clearErrorResultUseCase: ClearErrorResultUseCase,
    private val cameraRepository: CameraRepository
) : ViewModel() {
    
    val uiState: StateFlow<CameraModel> = cameraRepository.cameraState

    init {
        Log.d("CameraViewModel", "ViewModel created with repository instance: ${cameraRepository.hashCode()}")

        // Agregar logging para monitorear cambios de estado
        viewModelScope.launch {
            uiState.collect { state ->
                Log.d("CameraViewModel", "State changed in ViewModel from repository ${cameraRepository.hashCode()}: isInitialized=${state.isInitialized}, error=${state.error}")
            }
        }
    }

    fun initializeCamera(surfaceProvider: Any, lifecycleOwner: Any) {
        Log.d("CameraViewModel", "initializeCamera called from UI - using repository: ${cameraRepository.hashCode()}")
        viewModelScope.launch {
            Log.d("CameraViewModel","Starting initialization coroutine")
            initializeCameraResultUseCase(
                InitializeCameraResultUseCase.Params(surfaceProvider, lifecycleOwner)
            )
                .onSuccess {
                    Log.d("CameraViewModel","Successfully initialized camera")
                    // Verificar el estado después de la inicialización
                    Log.d("CameraViewModel", "Post-init state from repository ${cameraRepository.hashCode()}: isInitialized=${uiState.value.isInitialized}")
                }
                .onFailure { exception ->
                    // Handle error - already handled in use case and repository
                    android.util.Log.e("CameraViewModel", "Failed to initialize camera", exception)
                }
            Log.d("CameraViewModel","Initialization coroutine completed")
        }
    }

    fun capturePhoto() {
        viewModelScope.launch {

            if(!uiState.value.isInitialized){
                Log.e("CameraViewModel","Cannot capture photo: Camera not initializated")
                return@launch
            }

            capturePhotoUseCase()
                .onSuccess { uri ->
                    Log.d("CameraViewModel", "Photo captured: $uri")
                }
                .onFailure { exception ->
                    Log.e("CameraViewModel", "Failed to capture photo", exception)
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
            switchCameraResultUseCase(
                SwitchCameraResultUseCase.Params(surfaceProvider, lifecycleOwner)
            )
        }
    }

    fun toggleFlash() {
        viewModelScope.launch {
            toggleFlashUseCase()
        }
    }

    fun clearError() {
        viewModelScope.launch {
            clearErrorResultUseCase()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cameraRepository.cleanup()
    }



}