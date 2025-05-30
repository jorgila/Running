package com.estholon.running.ui.screen.camera

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(

) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CameraScreenViewState.CameraUIState())
    val uiState: StateFlow<CameraScreenViewState.CameraUIState> = _uiState.asStateFlow()

}