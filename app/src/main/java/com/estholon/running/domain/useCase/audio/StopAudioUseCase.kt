package com.estholon.running.domain.useCase.audio

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.repository.AudioRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class StopAudioUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) :BaseSuspendResultUseCase<StopAudioUseCase.StopAudioParams, Unit>() {

    data class StopAudioParams(
        val audio: AudioModel
    )

    override suspend fun execute(parameters: StopAudioParams) {
        val result = audioRepository.stopTrack(parameters.audio)
        result.getOrThrow()
    }

}