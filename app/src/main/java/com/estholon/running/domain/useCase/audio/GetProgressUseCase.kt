package com.estholon.running.domain.useCase.audio

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.model.AudioProgress
import com.estholon.running.domain.repository.AudioRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class GetProgressUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) : BaseSuspendResultUseCase<GetProgressUseCase.GetProgressParams, AudioProgress>(){

    data class GetProgressParams(
        val audio: AudioModel
    )

    override suspend fun execute(parameters: GetProgressParams) : AudioProgress {
        val result = audioRepository.getProgress(parameters.audio)
        result.fold(
            onSuccess = { progress ->
                return progress
            },
            onFailure = {
                return AudioProgress(0,0,0F)
            }
        )
    }

}