package com.estholon.running.ui.screen.camera

import android.Manifest
import android.os.Environment
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.estholon.running.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    runId: String,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    when {
        permissionsState.allPermissionsGranted -> {
            CameraContent(viewModel = viewModel)
        }
        permissionsState.shouldShowRationale -> {
            PermissionRationaleScreen(permissionsState = permissionsState)
        }
        else -> {
            PermissionRequestScreen(permissionsState = permissionsState)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestScreen(permissionsState: MultiplePermissionsState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Camera,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Camera Permissions Required",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "This app needs camera and audio permissions to take photos and record videos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = { permissionsState.launchMultiplePermissionRequest() }
            ) {
                Text("Grant Permissions")
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRationaleScreen(permissionsState: MultiplePermissionsState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = "Permissions Denied",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Camera and audio permissions are required for this app to function. Please grant them in settings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = { permissionsState.launchMultiplePermissionRequest() }
            ) {
                Text("Try Again")
            }
        }
    }
}

@Composable
fun CameraContent(viewModel: CameraViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    var showFlashEffect by remember { mutableStateOf(false) }

    // Flash effect
    LaunchedEffect(uiState.lastCapturedPhotoUri) {
        if (uiState.lastCapturedPhotoUri != null) {
            showFlashEffect = true
            delay(100)
            showFlashEffect = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                viewModel.initializeCamera(previewView.surfaceProvider, lifecycleOwner)
            }
        )

        // Flash Effect
        AnimatedVisibility(
            visible = showFlashEffect,
            enter = fadeIn(animationSpec = tween(50)),
            exit = fadeOut(animationSpec = tween(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.8f))
            )
        }

        // Top Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flash Toggle
            IconButton(
                onClick = { viewModel.toggleFlash() },
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (uiState.isFlashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Toggle Flash",
                    tint = Color.White
                )
            }

            // Recording Indicator
            if (uiState.isRecording) {
                RecordingIndicator(isPaused = uiState.isPaused)
            }

            // Camera Switch
            IconButton(
                onClick = {
                    // Need to get preview surface provider here - simplified for example
                    // In real implementation, you'd pass this from the AndroidView
                },
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera",
                    tint = Color.White
                )
            }
        }

        // Bottom Controls
        CameraControls(
            modifier = Modifier.align(Alignment.BottomCenter),
            isRecording = uiState.isRecording,
            isPaused = uiState.isPaused,
            onCapturePhoto = { viewModel.capturePhoto() },
            onStartRecording = { viewModel.startRecording() },
            onStopRecording = { viewModel.stopRecording() },
            onPauseRecording = { viewModel.pauseRecording() },
            onResumeRecording = { viewModel.resumeRecording() }
        )

        // Error Snackbar
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                delay(3000)
                viewModel.clearError()
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}

@Composable
fun RecordingIndicator(isPaused: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "recording")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recording_alpha"
    )

    Row(
        modifier = Modifier
            .background(
                Color.Black.copy(alpha = 0.7f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Canvas(
            modifier = Modifier.size(12.dp)
        ) {
            drawCircle(
                color = if (isPaused) Color.Yellow else Color.Red,
                alpha = if (isPaused) 1f else alpha
            )
        }

        Text(
            text = if (isPaused) "PAUSED" else "REC",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CameraControls(
    modifier: Modifier = Modifier,
    isRecording: Boolean,
    isPaused: Boolean,
    onCapturePhoto: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Photo Capture Button
        if (!isRecording) {
            FloatingActionButton(
                onClick = onCapturePhoto,
                modifier = Modifier.size(64.dp),
                containerColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Take Photo",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(64.dp))
        }

        // Video Recording Button
        FloatingActionButton(
            onClick = {
                when {
                    !isRecording -> onStartRecording()
                    else -> onStopRecording()
                }
            },
            modifier = Modifier
                .size(80.dp)
                .border(
                    4.dp,
                    if (isRecording) Color.Red else Color.White,
                    CircleShape
                ),
            containerColor = if (isRecording) Color.Red else Color.White
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Videocam,
                contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                tint = if (isRecording) Color.White else Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }

        // Pause/Resume Button (only visible during recording)
        if (isRecording) {
            FloatingActionButton(
                onClick = {
                    if (isPaused) onResumeRecording() else onPauseRecording()
                },
                modifier = Modifier.size(64.dp),
                containerColor = Color.Yellow
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Resume" else "Pause",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(64.dp))
        }
    }
}