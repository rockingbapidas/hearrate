package com.bapidas.heartrate.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bapidas.heartrate.R
import com.bapidas.heartrate.ui.fragment.viewmodel.HeartViewModel

@Composable
fun HeartRateScreen(viewModel: HeartViewModel) {
    val beatsPerMinute by viewModel.beatsPerMinute.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val isStarted by viewModel.isStarted.collectAsStateWithLifecycle()
    val measurementResult by viewModel.measurementResult.collectAsStateWithLifecycle()

    var showHintDialog by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress / 100f,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "Progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(250.dp),
                color = Color(0xFFF78828), // Orange
                strokeWidth = 8.dp,
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.place_finger),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Text(
                    text = beatsPerMinute,
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.hearts),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.bpm),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.startMeasurement() },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF78828))
        ) {
            Text(
                text = if (isStarted) stringResource(R.string.stop) else stringResource(R.string.start),
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showHintDialog = true },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
        ) {
            Text(
                text = stringResource(R.string.how_it_works),
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }

    if (showHintDialog) {
        AlertDialog(
            onDismissRequest = { showHintDialog = false },
            confirmButton = {
                TextButton(onClick = { showHintDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("How it works") },
            text = { Text("Place your finger on the camera lens and flash to measure your heart rate.") }
        )
    }

    measurementResult?.let { result ->
        when (result) {
            is HeartViewModel.MeasurementResult.Success -> {
                AlertDialog(
                    onDismissRequest = { viewModel.reset() },
                    confirmButton = {
                        TextButton(onClick = { viewModel.saveHeartRate() }) {
                            Text("SAVE")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.reset() }) {
                            Text("CANCEL")
                        }
                    },
                    title = { Text("Measurement Complete") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Your heart rate is:", fontSize = 16.sp)
                            Text(
                                text = "${result.bpm} BPM",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
            is HeartViewModel.MeasurementResult.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.reset() },
                    confirmButton = {
                        TextButton(onClick = { 
                            viewModel.reset()
                            viewModel.startMeasurement() 
                        }) {
                            Text("TRY AGAIN")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.reset() }) {
                            Text("CANCEL")
                        }
                    },
                    title = { Text("Measurement Error") },
                    text = { Text("Could not detect pulse. Please try again.") }
                )
            }
        }
    }
}