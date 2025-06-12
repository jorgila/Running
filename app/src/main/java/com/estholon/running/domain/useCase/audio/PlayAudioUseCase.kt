package com.estholon.running.domain.useCase.audio

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.repository.AudioRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class PlayAudioUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) : BaseSuspendResultUseCase<PlayAudioUseCase.PlayAudioParams, Unit>() {

    data class PlayAudioParams(
        val audio: AudioModel
    )

    override suspend fun execute(parameters: PlayAudioParams) {
        val result = audioRepository.playTrack(parameters.audio)
        result.getOrThrow()
    }

}