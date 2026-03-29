package com.bapidas.heartrate.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.bapidas.heartrate.R
import com.bapidas.heartrate.ui.fragment.viewmodel.HeartViewModel
import com.bapidas.heartrate.ui.fragment.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    heartViewModel: HeartViewModel,
    historyViewModel: HistoryViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.calculate), stringResource(R.string.history))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Heart Rate", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFFF78828)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> HeartRateScreen(heartViewModel)
                1 -> HistoryScreen(historyViewModel)
            }
        }
    }
}