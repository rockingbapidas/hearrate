package com.bapidas.heartrate.ui.base

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Created by bapidas on 08/11/17.
 */
abstract class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { setFragmentComponent(it) }
    }

    protected abstract fun setFragmentComponent(context: Context)

    protected abstract fun init()
}