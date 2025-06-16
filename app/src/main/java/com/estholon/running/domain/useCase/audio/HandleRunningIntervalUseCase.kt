package com.estholon.running.domain.useCase.audio

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class HandleRunningIntervalUseCase @Inject constructor(
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val playAudioUseCase: PlayAudioUseCase
) : BaseSuspendResultUseCase<HandleRunningIntervalUseCase.IntervalParams, Result<Unit>>() {

    data class IntervalParams(
        val isWalkingInterval: Boolean
    )

    override suspend fun execute(parameters: IntervalParams) : Result<Unit> {
        return try {
            if (parameters.isWalkingInterval) {
                // Change from running to walking
                pauseAudioUseCase(PauseAudioUseCase.PauseAudioParams(AudioModel.RUN)).getOrThrow()
                playAudioUseCase(PlayAudioUseCase.PlayAudioParams(AudioModel.NOTIFICATION)).getOrThrow()
                playAudioUseCase(PlayAudioUseCase.PlayAudioParams(AudioModel.WALK)).getOrThrow()
            } else {
                // Change from walking to running
                pauseAudioUseCase(PauseAudioUseCase.PauseAudioParams(AudioModel.WALK)).getOrThrow()
                playAudioUseCase(PlayAudioUseCase.PlayAudioParams(AudioModel.NOTIFICATION)).getOrThrow()
                playAudioUseCase(PlayAudioUseCase.PlayAudioParams(AudioModel.RUN)).getOrThrow()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}