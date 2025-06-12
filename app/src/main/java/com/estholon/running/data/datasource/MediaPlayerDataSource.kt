package com.estholon.running.data.datasource

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.estholon.running.R
import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.model.AudioProgress
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaPlayerDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioDataSource {

    private var runPlayer: MediaPlayer? = null
    private var walkPlayer: MediaPlayer? = null
    private var notificationPlayer: MediaPlayer? = null

    private val playerMap: MutableMap<AudioModel, MediaPlayer?> = mutableMapOf()

    override suspend fun initialize(): Result<Unit> =
        withContext(Dispatchers.IO){
            return@withContext runCatching {
                runPlayer = MediaPlayer.create(context, R.raw.hardmusic)?.apply {
                    isLooping = true
                } ?: throw IllegalStateException("Could not create run MediaPlayer")

                walkPlayer = MediaPlayer.create(context, R.raw.softmusic)?.apply {
                    isLooping = true
                } ?: throw IllegalStateException("Could not create walk MediaPlayer")

                notificationPlayer = MediaPlayer.create(context, R.raw.micmic)
                    ?: throw IllegalStateException("Could not create notification MediaPlayer")

                playerMap[AudioModel.RUN] = runPlayer
                playerMap[AudioModel.WALK] = walkPlayer
                playerMap[AudioModel.NOTIFICATION] = notificationPlayer

                Log.d("MediaPlayerDataSource", "Audio initialized successfully")
                Unit
            }
        }.onFailure { exception ->
            Log.e("MediaPlayerDataSource", "Failed to initialize audio", exception)
            release()
        }

    override suspend fun playTrack(audio: AudioModel): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val player = playerMap[audio]
                ?: throw IllegalStateException("Player for $audio not initialized")

            if (!player.isPlaying) {
                try {
                    player.start()
                    Log.d("MediaPlayerDataSource", "Successfully started playing $audio")
                } catch (e: IllegalStateException) {
                    // MediaPlayer puede estar en estado inválido
                    throw IllegalStateException("MediaPlayer for $audio is in invalid state", e)
                }
            } else {
                Log.d("MediaPlayerDataSource", "$audio was already playing")
            }
            Unit
        }.onFailure { exception ->
            Log.w("MediaPlayerDataSource", "Failed to play $audio", exception)
        }
    }

    override suspend fun pauseTrack(audio: AudioModel): Result<Unit> = withContext(Dispatchers.IO){
        return@withContext when(audio) {
            AudioModel.RUN, AudioModel.WALK -> {
                runCatching {
                    val player = playerMap[audio]
                        ?: throw IllegalStateException("Player for $audio not initialized")

                    when {
                        player.isPlaying -> {
                            try {
                                player.pause()
                                Log.d("MediaPlayerDataSource", "Successfully paused $audio")
                            } catch (e: IllegalStateException) {
                                throw IllegalStateException("MediaPlayer for $audio is in invalid state for pause", e)
                            }
                        }
                        else -> {
                            Log.d("MediaPlayerDataSource", "$audio was already paused or stopped")
                        }
                    }
                    Unit
                }.onFailure { exception ->
                    Log.w("MediaPlayerDataSource", "Failed to pause $audio", exception)
                }
            }
            AudioModel.NOTIFICATION -> {
                Log.d("MediaPlayerDataSource", "Pause operation not supported for NOTIFICATION track")
                Result.success(Unit)
            }
        }
    }

    override suspend fun stopTrack(audio: AudioModel): Result<Unit> =  withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val player = playerMap[audio]
                ?: throw IllegalStateException("Player for $audio not initialized")

            when {
                player.isPlaying -> {
                    try {
                        player.stop()
                        // Después de stop(), necesitamos prepare() para volver a usar el player
                        player.prepare()
                        Log.d("MediaPlayerDataSource", "Stopped and prepared $audio for next use")
                    } catch (e: IllegalStateException) {
                        throw IllegalStateException("MediaPlayer for $audio is in invalid state for stop", e)
                    } catch (e: Exception) {
                        throw IllegalStateException("Failed to prepare $audio after stop", e)
                    }
                }
                else -> {
                    Log.d("MediaPlayerDataSource", "$audio was not playing")
                }
            }
            Unit
        }.onFailure { exception ->
            Log.w("MediaPlayerDataSource", "Failed to stop $audio", exception)
        }
    }

    override suspend fun stopAll(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            var stoppedCount = 0
            var errorCount = 0

            playerMap.forEach { (audio, player) ->
                if (player != null) {
                    try {
                        if (player.isPlaying) {
                            player.pause() // Usar pause en lugar de stop para evitar problemas
                            player.seekTo(0) // Reset position
                            stoppedCount++
                            Log.d("MediaPlayerDataSource", "Stopped $audio")
                        }
                    } catch (e: Exception) {
                        errorCount++
                        Log.w("MediaPlayerDataSource", "Failed to stop $audio", e)
                    }
                }
            }

            Log.d("MediaPlayerDataSource",
                "Stop all completed: $stoppedCount stopped, $errorCount errors")

            if (errorCount > 0) {
                Log.w("MediaPlayerDataSource", "Some tracks failed to stop ($errorCount errors)")
            }

        }.onFailure { exception ->
            Log.e("MediaPlayerDataSource", "Critical error stopping all tracks", exception)
        }
    }

    override suspend fun setVolume(audio: AudioModel, volume: Float): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val player = playerMap[audio]
                ?: throw IllegalStateException("Player for $audio not initialized")

            val clampedVolume = volume.coerceIn(0f, 100f)
            val normalizedVolume = clampedVolume / 100f

            try {
                player.setVolume(normalizedVolume, normalizedVolume)
                Log.d("MediaPlayerDataSource",
                    "Set volume for $audio to ${clampedVolume}% (normalized: $normalizedVolume)")

                if (volume != clampedVolume) {
                    Log.w("MediaPlayerDataSource",
                        "Volume clamped from $volume to $clampedVolume for $audio")
                }
            } catch (e: IllegalStateException) {
                throw IllegalStateException("MediaPlayer for $audio is in invalid state for volume change", e)
            }

        }.onFailure { exception ->
            Log.w("MediaPlayerDataSource", "Failed to set volume for $audio to $volume%", exception)
        }
    }


    override suspend fun seekAudioMusic(audio: AudioModel, position: Float): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext when(audio) {
            AudioModel.RUN, AudioModel.WALK -> {
                runCatching {
                    val player = playerMap[audio]
                        ?: throw IllegalStateException("Player for $audio not initialized")

                    if (player.duration <= 0) {
                        throw IllegalStateException("Cannot seek $audio - invalid duration: ${player.duration}ms")
                    }

                    val clampedPosition = position.coerceIn(0f, 100f)
                    val targetPosition = ((player.duration * clampedPosition) / 100).toInt()

                    try {
                        player.seekTo(targetPosition)
                        Log.d("MediaPlayerDataSource",
                            "Seeked $audio to ${clampedPosition}% (${targetPosition}ms/${player.duration}ms)")
                    } catch (e: IllegalStateException) {
                        throw IllegalStateException("Cannot seek $audio - MediaPlayer in invalid state", e)
                    }
                    Unit

                }.onFailure { exception ->
                    Log.w("MediaPlayerDataSource", "Failed to seek music track $audio", exception)
                }
            }
            AudioModel.NOTIFICATION -> {
                Log.d("MediaPlayerDataSource", "Seek operation not supported for NOTIFICATION")
                Result.success(Unit)
            }
        }
    }

    override suspend fun isPlaying(audio: AudioModel): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val player = playerMap[audio]
                ?: throw IllegalStateException("Player for $audio not initialized")

            try {
                val playing = player.isPlaying
                Log.d("MediaPlayerDataSource", "$audio is ${if (playing) "playing" else "not playing"}")
                playing
            } catch (e: IllegalStateException) {
                Log.w("MediaPlayerDataSource", "MediaPlayer for $audio in invalid state, assuming not playing")
                false
            }
        }.onFailure { exception ->
            Log.w("MediaPlayerDataSource", "Failed to check playing state for $audio", exception)
        }
    }

    override suspend fun getProgress(audio: AudioModel): Result<AudioProgress> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val player = playerMap[audio]
                ?: throw IllegalStateException("Player for $audio not initialized")

            val current = try {
                player.currentPosition.toLong().coerceAtLeast(0)
            } catch (e: IllegalStateException) {
                Log.w("MediaPlayerDataSource", "Cannot get current position for $audio", e)
                0L
            }

            val duration = try {
                player.duration.toLong().coerceAtLeast(0)
            } catch (e: IllegalStateException) {
                Log.w("MediaPlayerDataSource", "Cannot get duration for $audio", e)
                0L
            }

            val percentage = if (duration > 0) {
                ((current.toFloat() / duration.toFloat()) * 100).coerceIn(0f, 100f)
            } else 0f

            AudioProgress(current, duration, percentage).also { progress ->
                Log.d("MediaPlayerDataSource",
                    "$audio progress: ${current}ms/${duration}ms (${percentage.toInt()}%)")
            }

        }.onFailure { exception ->
            Log.w("MediaPlayerDataSource", "Failed to get progress for $audio", exception)
        }
    }

    override suspend fun release(): Result<Unit> = withContext(Dispatchers.IO){
        return@withContext runCatching {
            playerMap.values.forEach { player ->
                player?.release()
            }
            playerMap.clear()
            runPlayer = null
            walkPlayer = null
            notificationPlayer = null
            Log.d("MediaPlayerDataSource", "Audio resources released")
            Unit
        }
    }

}