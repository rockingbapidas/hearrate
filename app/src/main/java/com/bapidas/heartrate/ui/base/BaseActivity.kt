package com.bapidas.heartrate.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by bapidas on 09/10/17.
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityComponent()
    }

    protected abstract fun setActivityComponent()
    protected abstract fun init()
}