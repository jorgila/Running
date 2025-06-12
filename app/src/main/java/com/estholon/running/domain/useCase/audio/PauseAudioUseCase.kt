package com.estholon.running.domain.useCase.audio

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.repository.AudioRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class PauseAudioUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) : BaseSuspendResultUseCase<PauseAudioUseCase.PauseAudioParams,Unit>() {

    data class PauseAudioParams(
        val audio: AudioModel
    )

    override suspend fun execute(parameters: PauseAudioParams) {
        val result = audioRepository.pauseTrack(parameters.audio)
        result.getOrThrow()
    }

}