package com.bapidas.heartrate.camera

import android.content.Context
import com.bapidas.heartrate.processing.Processing
import com.bapidas.heartrate.processing.ProcessingSupport
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraSupportImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CameraSupport {

    private val processingSupport: ProcessingSupport = Processing()
    private val delegate: CameraSupport = CameraNew(context, processingSupport)

    override fun open(): CameraSupport {
        return delegate.open()
    }

    override fun close() {
        delegate.close()
    }

    override val isCameraInUse: Boolean
        get() = delegate.isCameraInUse

    override fun addOnPreviewListener(callBack: PreviewListener) {
        delegate.addOnPreviewListener(callBack)
    }
}