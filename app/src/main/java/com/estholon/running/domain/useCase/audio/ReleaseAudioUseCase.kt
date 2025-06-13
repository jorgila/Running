package com.estholon.running.domain.useCase.audio

import com.estholon.running.domain.repository.AudioRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCaseNoParams
import javax.inject.Inject

class ReleaseAudioUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) : BaseSuspendResultUseCaseNoParams<Unit>() {

    override suspend fun execute() {
        val result = audioRepository.release()
        result.getOrThrow()
    }

}