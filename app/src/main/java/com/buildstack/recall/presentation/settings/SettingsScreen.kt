package com.buildstack.recall.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.buildstack.recall.presentation.components.GlassCard
import com.buildstack.recall.theme.ButtonGlow
import com.buildstack.recall.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val is24HourFormat by viewModel.is24HourFormat.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            GlassCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "24-Hour Time Format", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = "Use 24-hour format instead of AM/PM", color = White.copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                        Switch(
                            checked = is24HourFormat,
                            onCheckedChange = viewModel::toggle24HourFormat,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = White,
                                checkedTrackColor = ButtonGlow
                            )
                        )
                    }

                    androidx.compose.material3.Divider(color = White.copy(alpha = 0.2f))

                    val isDailySummaryEnabled by viewModel.isDailySummaryEnabled.collectAsStateWithLifecycle()
                    val dailySummaryTime by viewModel.dailySummaryTime.collectAsStateWithLifecycle()
                    var showTimePicker by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Daily Summary", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = "Get a morning notification of today's reminders", color = White.copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                        Switch(
                            checked = isDailySummaryEnabled,
                            onCheckedChange = viewModel::toggleDailySummary,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = White,
                                checkedTrackColor = ButtonGlow
                            )
                        )
                    }

                    if (isDailySummaryEnabled) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Summary Time", color = White, fontSize = 16.sp)
                            Text(text = dailySummaryTime, color = ButtonGlow, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    if (showTimePicker) {
                        val timeState = androidx.compose.material3.rememberTimePickerState(
                            initialHour = dailySummaryTime.split(":")[0].toIntOrNull() ?: 8,
                            initialMinute = dailySummaryTime.split(":")[1].toIntOrNull() ?: 0,
                            is24Hour = is24HourFormat
                        )
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { showTimePicker = false },
                            confirmButton = {
                                androidx.compose.material3.TextButton(onClick = {
                                    val h = timeState.hour
                                    val m = timeState.minute
                                    viewModel.updateDailySummaryTime(String.format("%02d:%02d", h, m))
                                    showTimePicker = false
                                }) { Text("OK", color = ButtonGlow) }
                            },
                            dismissButton = {
                                androidx.compose.material3.TextButton(onClick = { showTimePicker = false }) { Text("Cancel", color = White) }
                            },
                            text = {
                                androidx.compose.material3.TimePicker(state = timeState)
                            },
                            containerColor = Color(0xFF1E1E1E)
                        )
                    }

                    androidx.compose.material3.HorizontalDivider(color = White.copy(alpha = 0.2f))

                    val snoozeDuration by viewModel.snoozeDuration.collectAsStateWithLifecycle()
                    var snoozeDropdownExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                    val snoozeOptions = listOf(1, 5, 10, 15)

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { snoozeDropdownExpanded = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Snooze Duration", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = "Time to delay the alarm", color = White.copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                        Box {
                            Text(text = "$snoozeDuration min", color = ButtonGlow, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            androidx.compose.material3.DropdownMenu(
                                expanded = snoozeDropdownExpanded,
                                onDismissRequest = { snoozeDropdownExpanded = false }
                            ) {
                                snoozeOptions.forEach { option ->
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("$option minutes") },
                                        onClick = {
                                            viewModel.updateSnoozeDuration(option)
                                            snoozeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
