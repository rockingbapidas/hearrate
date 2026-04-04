package com.bapidas.heartrate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bapidas.heartrate.R
import com.bapidas.heartrate.data.model.HistoryModel
import com.bapidas.heartrate.ui.fragment.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val historyList by viewModel.history.collectAsStateWithLifecycle()

    if (historyList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No history available", color = Color.White)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(historyList) { history ->
                HistoryItem(history)
            }
        }
    }
}

@Composable
fun HistoryItem(history: HistoryModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.hearts),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.heartRate,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "BPM",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = history.dateString,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = history.timeString,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}