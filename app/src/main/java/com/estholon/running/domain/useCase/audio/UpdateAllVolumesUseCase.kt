package com.estholon.running.domain.useCase.audio

import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class UpdateAllVolumesUseCase @Inject constructor(
    private val setAudioVolumeUseCase: SetAudioVolumeUseCase
) : BaseSuspendResultUseCase<UpdateAllVolumesUseCase.VolumeParams,Result<Unit>>() {

    data class VolumeParams(
        val runVolume: Float,
        val walkVolume: Float,
        val notificationVolume: Float
    )

    override suspend fun execute(parameters: VolumeParams) : Result<Unit> {
        return try {
            setAudioVolumeUseCase(SetAudioVolumeUseCase.SetVolumeParams(AudioModel.RUN, parameters.runVolume)).getOrThrow()
            setAudioVolumeUseCase(SetAudioVolumeUseCase.SetVolumeParams(AudioModel.WALK, parameters.walkVolume)).getOrThrow()
            setAudioVolumeUseCase(SetAudioVolumeUseCase.SetVolumeParams(AudioModel.NOTIFICATION, parameters.notificationVolume)).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}