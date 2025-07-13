package com.estholon.running.ui.screen.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

@Composable
fun VideoPlayer(
    videoUri: Uri,
    modifier: Modifier = Modifier
) {
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var hasError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    Box(modifier = modifier) {
        if (hasError) {
            // Error state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error al cargar video\n$errorMessage",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else {
            // Video player
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    VideoView(context).apply {
                        setOnPreparedListener { mediaPlayer ->
                            isLoading = false
                            mediaPlayer.isLooping = true
                            mediaPlayer.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                            start()
                        }

                        setOnErrorListener { _, what, extra ->
                            isLoading = false
                            hasError = true
                            errorMessage = "Error code: $what, Extra: $extra"
                            true
                        }

                        setOnCompletionListener {
                            // Video completed
                        }

                        // Set the video URI
                        try {
                            val uri = videoUri
                            setVideoURI(uri)
                        } catch (e: Exception) {
                            isLoading = false
                            hasError = true
                            errorMessage = "URI inválida: ${e.message}"
                        }
                    }
                },
                update = { videoView ->
                    try {
                        val uri = videoUri
                        videoView.setVideoURI(uri)
                    } catch (e: Exception) {
                        hasError = true
                        errorMessage = "Error al actualizar video: ${e.message}"
                    }
                }
            )

            // Loading state overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = Color.White
                    )
                }
            }
        }
    }
}
