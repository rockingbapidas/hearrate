package com.bapidas.heartrate.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class ToolsUtilsTest {

    @Test
    fun `getTime returns correctly formatted time in English`() {
        // Fix the timezone to ensure consistent results regardless of the environment
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        
        val timestamp = 1635768000000L // 2021-11-01 12:00:00 UTC
        val expectedTime = "12:00 PM"
        val actualTime = ToolsUtils.getTime(timestamp)
        
        assertEquals(expectedTime, actualTime)
    }

    @Test
    fun `getDate returns correctly formatted date in English`() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        
        val timestamp = 1635768000000L // 2021-11-01 12:00:00 UTC
        val expectedDate = "01/11/2021"
        val actualDate = ToolsUtils.getDate(timestamp)
        
        assertEquals(expectedDate, actualDate)
    }
}
