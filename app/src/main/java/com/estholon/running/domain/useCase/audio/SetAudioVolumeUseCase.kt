package com.estholon.running.domain.useCase.audio

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.repository.AudioRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class SetAudioVolumeUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) : BaseSuspendResultUseCase<SetAudioVolumeUseCase.SetVolumeParams,Unit>() {

    data class SetVolumeParams(
        val audio: AudioModel,
        val volume: Float
    )

    override suspend fun execute(parameters: SetVolumeParams) {
        val result = audioRepository.setVolume(parameters.audio,parameters.volume)
        result.getOrThrow()
    }

}