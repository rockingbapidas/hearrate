package com.bapidas.heartrate.camera

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CameraSupportImplTest {

    @Mock
    private lateinit var context: Context
    
    // Since CameraSupportImpl creates CameraNew internally, we might need to adjust how we test it
    // Or refactor it to accept CameraSupport as a dependency for better testability.
    // For now, let's test the current implementation by mocking the context.
    
    private lateinit var cameraSupportImpl: CameraSupportImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        cameraSupportImpl = CameraSupportImpl(context)
    }

    @Test
    fun `isCameraInUse returns false initially`() {
        assertEquals(false, cameraSupportImpl.isCameraInUse)
    }
}
