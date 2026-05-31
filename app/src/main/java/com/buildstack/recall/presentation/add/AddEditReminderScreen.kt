package com.buildstack.recall.presentation.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.buildstack.recall.domain.model.PriorityLevel
import com.buildstack.recall.presentation.components.GlassCard
import com.buildstack.recall.theme.ButtonGlow
import com.buildstack.recall.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReminderScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.id == null) "New Reminder" else "Edit Reminder", color = White) },
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
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = viewModel::updateTitle,
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = ButtonGlow,
                            unfocusedBorderColor = White.copy(alpha = 0.3f),
                            focusedLabelColor = ButtonGlow,
                            unfocusedLabelColor = White.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = viewModel::updateDescription,
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = ButtonGlow,
                            unfocusedBorderColor = White.copy(alpha = 0.3f),
                            focusedLabelColor = ButtonGlow,
                            unfocusedLabelColor = White.copy(alpha = 0.5f)
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // We keep the OutlinedTextField but make them readOnly and clickable
                        var showTimePicker by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                        val is24HourFormat by viewModel.is24HourFormat.collectAsStateWithLifecycle()

                        OutlinedTextField(
                            value = state.reminderDate,
                            onValueChange = viewModel::updateDate,
                            label = { Text("Date (MM/DD/YYYY)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White, unfocusedTextColor = White
                            )
                        )
                        OutlinedTextField(
                            value = state.reminderTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Time (HH:MM)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White, unfocusedTextColor = White,
                                disabledTextColor = White
                            ),
                            trailingIcon = {
                                IconButton(onClick = { showTimePicker = true }) {
                                    Text("🕒", color = White)
                                }
                            }
                        )

                        if (showTimePicker) {
                            val timeState = androidx.compose.material3.rememberTimePickerState(
                                is24Hour = is24HourFormat
                            )
                            androidx.compose.material3.AlertDialog(
                                onDismissRequest = { showTimePicker = false },
                                confirmButton = {
                                    androidx.compose.material3.TextButton(onClick = {
                                        val h = timeState.hour
                                        val m = timeState.minute
                                        viewModel.updateTime(String.format("%02d:%02d", h, m))
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
                    }

                    // Category Selection
                    OutlinedTextField(
                        value = state.category,
                        onValueChange = { viewModel.updateCategory(it) },
                        label = { Text("Category (e.g. Work, Personal)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White, unfocusedTextColor = White,
                            focusedBorderColor = ButtonGlow, unfocusedBorderColor = White.copy(alpha = 0.3f),
                            focusedLabelColor = ButtonGlow, unfocusedLabelColor = White.copy(alpha = 0.5f)
                        )
                    )

                    // Priority Selection
                    Text("Priority", color = White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PriorityLevel.entries.forEach { priority ->
                            Button(
                                onClick = { viewModel.updatePriority(priority) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.priority == priority) ButtonGlow else Color.Transparent,
                                    contentColor = White
                                ),
                                shape = RoundedCornerShape(50.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(priority.label)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.saveReminder(onNavigateBack) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonGlow),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                        Text("Save Reminder", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}
