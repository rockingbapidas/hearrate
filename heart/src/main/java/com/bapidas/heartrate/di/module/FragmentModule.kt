package com.bapidas.heartrate.di.module

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.bapidas.heartrate.camera.CameraNew
import com.bapidas.heartrate.camera.CameraOld
import com.bapidas.heartrate.camera.CameraSupport
import com.bapidas.heartrate.core.HeartRate
import com.bapidas.heartrate.di.annotation.ActivityContext
import com.bapidas.heartrate.di.annotation.ApplicationContext
import com.bapidas.heartrate.processing.Processing
import com.bapidas.heartrate.processing.ProcessingSupport
import dagger.Module
import dagger.Provides

/**
 * Created by bapidas on 14/11/17.
 */
@Module
class FragmentModule(private val mFragment: Fragment) {
    @Provides
    @ActivityContext
    fun provideContext(): Activity {
        return mFragment.requireActivity()
    }

    @Provides
    fun provideFragment(): Fragment {
        return mFragment
    }

    @Provides
    fun provideProcessingSupport(): ProcessingSupport {
        return Processing()
    }

    @Provides
    fun provideCameraSupport(
        @ApplicationContext mContext: Context,
        @ActivityContext mActivity: Activity,
        processingSupport: ProcessingSupport
    ): CameraSupport {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraNew(mContext, processingSupport)
        } else {
            CameraOld(mActivity, processingSupport)
        }
    }

    @Provides
    fun provideHeartRate(cameraSupport: CameraSupport): HeartRate {
        return HeartRate(cameraSupport)
    }

}