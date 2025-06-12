package com.estholon.running.data.repository

import com.estholon.running.data.datasource.AudioDataSource
import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.model.AudioProgress
import com.estholon.running.domain.repository.AudioRepository
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(
    private val audioDataSource: AudioDataSource
) : AudioRepository {

    override fun initialize(): Result<Unit> {
        return audioDataSource.initialize()
    }

    override fun playTrack(audio: AudioModel): Result<Unit> {
        return audioDataSource.playTrack(audio)
    }

    override fun pauseTrack(audio: AudioModel): Result<Unit> {
        return audioDataSource.pauseTrack(audio)
    }

    override fun stopTrack(audio: AudioModel): Result<Unit> {
        return audioDataSource.stopTrack(audio)
    }

    override fun stopAll(): Result<Unit> {
        return audioDataSource.stopAll()
    }

    override fun setVolume(audio: AudioModel, volume: Float): Result<Unit> {
        return audioDataSource.setVolume(audio,volume)
    }

    override fun seekAudioMusic(audio: AudioModel, position: Float): Result<Unit> {
        return audioDataSource.seekAudioMusic(audio,position)
    }

    override fun isPlaying(audio: AudioModel): Result<Boolean> {
        return audioDataSource.isPlaying(audio)
    }

    override fun getProgress(): Result<AudioProgress> {
        return audioDataSource.getProgress()
    }

    override fun release(): Result<Unit> {
        return audioDataSource.release()
    }
}