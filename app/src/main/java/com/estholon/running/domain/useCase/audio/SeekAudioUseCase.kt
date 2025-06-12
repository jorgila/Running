package com.estholon.running.domain.useCase.audio

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.repository.AudioRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class SeekAudioUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) : BaseSuspendResultUseCase<SeekAudioUseCase.SeekAudioParams,Unit>() {

    data class SeekAudioParams(
        val audio: AudioModel,
        val position: Float
    )

    override suspend fun execute(parameters: SeekAudioUseCase.SeekAudioParams) {
        val result = audioRepository.seekAudioMusic(parameters.audio,parameters.position)
        result.getOrThrow()
    }

}