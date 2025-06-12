package com.estholon.running.data.repository

import com.estholon.running.data.datasource.AudioDataSource
import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.model.AudioProgress
import com.estholon.running.domain.repository.AudioRepository
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(
    private val audioDataSource: AudioDataSource
) : AudioRepository {

    override suspend fun initialize(): Result<Unit> {
        return audioDataSource.initialize()
    }

    override suspend fun playTrack(audio: AudioModel): Result<Unit> {
        return audioDataSource.playTrack(audio)
    }

    override suspend fun pauseTrack(audio: AudioModel): Result<Unit> {
        return audioDataSource.pauseTrack(audio)
    }

    override suspend fun stopTrack(audio: AudioModel): Result<Unit> {
        return audioDataSource.stopTrack(audio)
    }

    override suspend fun stopAll(): Result<Unit> {
        return audioDataSource.stopAll()
    }

    override suspend fun setVolume(audio: AudioModel, volume: Float): Result<Unit> {
        return audioDataSource.setVolume(audio,volume)
    }

    override suspend fun seekAudioMusic(audio: AudioModel, position: Float): Result<Unit> {
        return audioDataSource.seekAudioMusic(audio,position)
    }

    override suspend fun isPlaying(audio: AudioModel): Result<Boolean> {
        return audioDataSource.isPlaying(audio)
    }

    override suspend fun getProgress(audio: AudioModel): Result<AudioProgress> {
        return audioDataSource.getProgress(audio)
    }

    override suspend fun release(): Result<Unit> {
        return audioDataSource.release()
    }
}