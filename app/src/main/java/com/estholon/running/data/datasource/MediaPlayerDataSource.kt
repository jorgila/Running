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

    private val playerMap: Map<AudioModel, MediaPlayer?>
        get() = mapOf(
            AudioModel.RUN to runPlayer,
            AudioModel.WALK to walkPlayer,
            AudioModel.NOTIFICATION to notificationPlayer
        )


    override suspend fun initialize(): Result<Unit> {
        withContext(Dispatchers.IO){
            runCatching {
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
            }.onFailure { exception ->
                Log.e("MediaPlayerDataSource", "Failed to initialize audio", exception)
                release()
            }
        }
    }

    override suspend fun playTrack(audio: AudioModel): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun pauseTrack(audio: AudioModel): Result<Unit> {
        TODO()
    }

    override suspend fun stopTrack(audio: AudioModel): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun stopAll(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun setVolume(audio: AudioModel, volume: Float): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun seekAudioMusic(audio: AudioModel, position: Float): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun isPlaying(audio: AudioModel): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getProgress(): Result<AudioProgress> {
        TODO("Not yet implemented")
    }

    override suspend fun release(): Result<Unit> {
        TODO("Not yet implemented")
    }

}