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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
                            onValueChange = viewModel::updateTime,
                            label = { Text("Time (HH:MM)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White, unfocusedTextColor = White
                            )
                        )
                    }

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
