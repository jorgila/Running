package com.estholon.running.domain.repository

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.model.AudioProgress

interface AudioRepository {

    fun initialize() : Result<Unit>
    fun playTrack(audio: AudioModel) : Result<Unit>
    fun pauseTrack(audio: AudioModel) : Result<Unit>
    fun stopTrack(audio: AudioModel) : Result<Unit>
    fun stopAll() : Result<Unit>
    fun setVolume(audio: AudioModel, volume: Float) : Result<Unit>
    fun seekAudioMusic(audio: AudioModel, position: Float) : Result<Unit>
    fun isPlaying(audio: AudioModel) : Result<Boolean>
    fun getProgress() : Result<AudioProgress>
    fun release() : Result<Unit>

}

