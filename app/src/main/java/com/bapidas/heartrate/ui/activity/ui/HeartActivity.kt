package com.bapidas.heartrate.ui.activity.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bapidas.heartrate.HeartApplication.Companion.get
import com.bapidas.heartrate.R
import com.bapidas.heartrate.databinding.MainActivityBinding
import com.bapidas.heartrate.di.component.ActivityComponent
import com.bapidas.heartrate.di.component.DaggerActivityComponent
import com.bapidas.heartrate.di.module.ActivityModule
import com.bapidas.heartrate.ui.activity.presenter.HeartActivityPresenter
import com.bapidas.heartrate.ui.base.BaseActivity
import com.bapidas.heartrate.utils.Constant
import com.bapidas.heartrate.utils.ToolsUtils

class HeartActivity : BaseActivity() {
    private val TAG = HeartActivity::class.java.simpleName
    var mActionBar: ActionBar? = null
    var mBinding: MainActivityBinding? = null
    private var mActivityComponent: ActivityComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        askPermission()
        init()
    }

    override fun setActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .appComponent(get(this).appComponent)
                .build()
        }
        mActivityComponent?.inject(this)
    }

    override fun init() {
        val mHeartActivityPresenter = HeartActivityPresenter(this)
        mHeartActivityPresenter.setup()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermission(requestCode, grantResults)
    }

    private fun askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ToolsUtils.isHasPermissions(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                Log.d(TAG, "Permission already accepted")
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    Constant.REQUEST_CAMERA_PERMISSION
                )
            }
        } else {
            Log.d(TAG, "No need permission")
        }
    }

    private fun checkPermission(requestCode: Int, grantResults: IntArray) {
        if (requestCode == Constant.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Permission granted")
            } else {
                Log.d(TAG, "Permission not granted")
                Toast.makeText(
                    this, "You have to give permission " +
                            "to access this window", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}