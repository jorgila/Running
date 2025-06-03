package com.estholon.running.data.di

import android.content.Context
import com.estholon.running.data.repository.CameraRepositoryImpl
import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.camera.CapturePhotoUseCase
import com.estholon.running.domain.useCase.camera.ClearErrorUseCase
import com.estholon.running.domain.useCase.camera.InitializeCameraUseCase
import com.estholon.running.domain.useCase.camera.PauseVideoRecordingUseCase
import com.estholon.running.domain.useCase.camera.ResumeVideoRecordingUseCase
import com.estholon.running.domain.useCase.camera.StartVideoRecordingUseCase
import com.estholon.running.domain.useCase.camera.StopVideoRecordingUseCase
import com.estholon.running.domain.useCase.camera.SwitchCameraUseCase
import com.estholon.running.domain.useCase.camera.ToggleFlashUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
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
    ): InitializeCameraUseCase {
        return InitializeCameraUseCase(cameraRepository)
    }

    @Provides
    fun provideCapturePhotoUseCase(
        cameraRepository: CameraRepository
    ): CapturePhotoUseCase {
        return CapturePhotoUseCase(cameraRepository)
    }

    @Provides
    fun provideStartVideoRecordingUseCase(
        cameraRepository: CameraRepository
    ): StartVideoRecordingUseCase {
        return StartVideoRecordingUseCase(cameraRepository)
    }

    @Provides
    fun provideStopVideoRecordingUseCase(
        cameraRepository: CameraRepository
    ): StopVideoRecordingUseCase {
        return StopVideoRecordingUseCase(cameraRepository)
    }

    @Provides
    fun providePauseVideoRecordingUseCase(
        cameraRepository: CameraRepository
    ): PauseVideoRecordingUseCase {
        return PauseVideoRecordingUseCase(cameraRepository)
    }

    @Provides
    fun provideResumeRecordingUseCase(
        cameraRepository: CameraRepository
    ): ResumeVideoRecordingUseCase {
        return ResumeVideoRecordingUseCase(cameraRepository)
    }

    @Provides
    fun provideSwitchCameraUseCase(
        cameraRepository: CameraRepository
    ): SwitchCameraUseCase {
        return SwitchCameraUseCase(cameraRepository)
    }

    @Provides
    fun provideToggleFlashUseCase(
        cameraRepository: CameraRepository
    ): ToggleFlashUseCase {
        return ToggleFlashUseCase(cameraRepository)
    }

    @Provides
    fun provideClearErrorUseCase(
        cameraRepository: CameraRepository
    ): ClearErrorUseCase {
        return ClearErrorUseCase(cameraRepository)
    }

}