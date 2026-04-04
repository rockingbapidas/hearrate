package com.bapidas.heartrate.di

import com.bapidas.heartrate.camera.CameraSupport
import com.bapidas.heartrate.camera.CameraSupportImpl
import com.bapidas.heartrate.core.HeartRate
import com.bapidas.heartrate.core.HeartSupport
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindCameraSupport(impl: CameraSupportImpl): CameraSupport

    companion object {
        @Provides
        @Singleton
        fun provideHeartSupport(cameraSupport: CameraSupport): HeartSupport {
            return HeartRate(cameraSupport)
        }
    }
}