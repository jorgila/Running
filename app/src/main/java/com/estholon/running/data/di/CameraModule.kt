package com.estholon.running.data.di

import android.content.Context
import com.estholon.running.data.repository.CameraRepositoryImpl
import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.camera.CapturePhotoResultUseCase
import com.estholon.running.domain.useCase.camera.ClearErrorResultUseCase
import com.estholon.running.domain.useCase.camera.InitializeCameraResultUseCase
import com.estholon.running.domain.useCase.camera.PauseVideoRecordingResultUseCase
import com.estholon.running.domain.useCase.camera.ResumeVideoRecordingResultUseCase
import com.estholon.running.domain.useCase.camera.StartVideoRecordingResultUseCase
import com.estholon.running.domain.useCase.camera.StopVideoRecordingResultUseCase
import com.estholon.running.domain.useCase.camera.SwitchCameraResultUseCase
import com.estholon.running.domain.useCase.camera.ToggleFlashResultUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CameraModule {

    @Provides
    @Singleton
    fun provideCameraRepository(
        @ApplicationContext context: Context
    ): CameraRepository {
        return CameraRepositoryImpl(context)
    }

    @Provides
    fun provideInitializeCameraUseCase(
        cameraRepository: CameraRepository
    ): InitializeCameraResultUseCase {
        return InitializeCameraResultUseCase(cameraRepository)
    }

    @Provides
    fun provideCapturePhotoUseCase(
        cameraRepository: CameraRepository
    ): CapturePhotoResultUseCase {
        return CapturePhotoResultUseCase(cameraRepository)
    }

    @Provides
    fun provideStartVideoRecordingUseCase(
        cameraRepository: CameraRepository
    ): StartVideoRecordingResultUseCase {
        return StartVideoRecordingResultUseCase(cameraRepository)
    }

    @Provides
    fun provideStopVideoRecordingUseCase(
        cameraRepository: CameraRepository
    ): StopVideoRecordingResultUseCase {
        return StopVideoRecordingResultUseCase(cameraRepository)
    }

    @Provides
    fun providePauseVideoRecordingUseCase(
        cameraRepository: CameraRepository
    ): PauseVideoRecordingResultUseCase {
        return PauseVideoRecordingResultUseCase(cameraRepository)
    }

    @Provides
    fun provideResumeRecordingUseCase(
        cameraRepository: CameraRepository
    ): ResumeVideoRecordingResultUseCase {
        return ResumeVideoRecordingResultUseCase(cameraRepository)
    }

    @Provides
    fun provideSwitchCameraUseCase(
        cameraRepository: CameraRepository
    ): SwitchCameraResultUseCase {
        return SwitchCameraResultUseCase(cameraRepository)
    }

    @Provides
    fun provideToggleFlashUseCase(
        cameraRepository: CameraRepository
    ): ToggleFlashResultUseCase {
        return ToggleFlashResultUseCase(cameraRepository)
    }

    @Provides
    fun provideClearErrorUseCase(
        cameraRepository: CameraRepository
    ): ClearErrorResultUseCase {
        return ClearErrorResultUseCase(cameraRepository)
    }

}