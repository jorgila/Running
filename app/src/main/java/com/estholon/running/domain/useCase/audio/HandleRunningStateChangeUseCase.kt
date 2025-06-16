package com.estholon.running.domain.useCase.audio

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class HandleRunningStateChangeUseCase @Inject constructor(
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val playAudioUseCase: PlayAudioUseCase
) : BaseSuspendResultUseCase<HandleRunningStateChangeUseCase.StateChangeParams, Result<Unit>>() {

    data class StateChangeParams(
        val isPaused: Boolean,
        val isWalkingInterval: Boolean
    )

    override suspend fun execute(parameters: StateChangeParams) : Result<Unit> {
        return try {
            val audioModel = if (parameters.isWalkingInterval) AudioModel.WALK else AudioModel.RUN

            if (parameters.isPaused) {
                pauseAudioUseCase(PauseAudioUseCase.PauseAudioParams(audioModel)).getOrThrow()
            } else {
                playAudioUseCase(PlayAudioUseCase.PlayAudioParams(audioModel)).getOrThrow()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}