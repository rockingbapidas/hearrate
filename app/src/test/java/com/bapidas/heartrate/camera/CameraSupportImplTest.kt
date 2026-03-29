package com.bapidas.heartrate.camera

import android.content.Context
import android.os.PowerManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CameraSupportImplTest {

    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var powerManager: PowerManager
    @Mock
    private lateinit var wakeLock: PowerManager.WakeLock

    private lateinit var cameraSupportImpl: CameraSupportImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(context.getSystemService(Context.POWER_SERVICE)).thenReturn(powerManager)
        `when`(powerManager.newWakeLock(anyInt(), anyString())).thenReturn(wakeLock)
        cameraSupportImpl = CameraSupportImpl(context)
    }

    private fun anyInt() = org.mockito.ArgumentMatchers.anyInt()
    private fun anyString() = org.mockito.ArgumentMatchers.anyString()

    @Test
    fun `isCameraInUse returns false initially`() {
        assertEquals(false, cameraSupportImpl.isCameraInUse)
    }
}
