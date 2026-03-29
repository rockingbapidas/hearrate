package com.bapidas.heartrate.ui.activity.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bapidas.heartrate.ui.fragment.viewmodel.HeartViewModel
import com.bapidas.heartrate.ui.fragment.viewmodel.HistoryViewModel
import com.bapidas.heartrate.ui.screens.MainScreen
import com.bapidas.heartrate.ui.theme.HeartRateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HeartActivity : ComponentActivity() {
    private val TAG = HeartActivity::class.java.simpleName
    private val heartViewModel: HeartViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Permission granted")
        } else {
            Log.d(TAG, "Permission not granted")
            Toast.makeText(
                this, "You have to give permission to access camera",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkAndRequestPermission()

        setContent {
            HeartRateTheme {
                MainScreen(
                    heartViewModel = heartViewModel,
                    historyViewModel = historyViewModel
                )
            }
        }
    }

    private fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "Permission already accepted")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}