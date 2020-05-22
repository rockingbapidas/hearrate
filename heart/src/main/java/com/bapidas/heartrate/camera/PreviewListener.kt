package com.bapidas.heartrate.camera

/**
 * Created by SiD on 10/14/2017.
 */
interface PreviewListener {
    fun onPreviewData(data: ByteArray?)
    fun onPreviewCount(count: Int)
}