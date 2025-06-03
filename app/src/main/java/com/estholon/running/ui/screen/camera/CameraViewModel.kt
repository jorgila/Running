package com.estholon.running.ui.screen.camera

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlinx.coroutines.flow.collect
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
            initializeCameraUseCase(surfaceProvider, lifecycleOwner)
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