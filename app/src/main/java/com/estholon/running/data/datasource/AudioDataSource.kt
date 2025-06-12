package com.estholon.running.data.datasource

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.model.AudioProgress

interface AudioDataSource {

    suspend fun initialize() : Result<Unit>
    suspend fun playTrack(audio: AudioModel) : Result<Unit>
    suspend fun pauseTrack(audio: AudioModel) : Result<Unit>
    suspend fun stopTrack(audio: AudioModel) : Result<Unit>
    suspend fun stopAll() : Result<Unit>
    suspend fun setVolume(audio: AudioModel, volume: Float) : Result<Unit>
    suspend fun seekAudioMusic(audio: AudioModel, position: Float) : Result<Unit>
    suspend fun isPlaying(audio: AudioModel) : Result<Boolean>
    suspend fun getProgress(audio: AudioModel) : Result<AudioProgress>
    suspend fun release() : Result<Unit>

}