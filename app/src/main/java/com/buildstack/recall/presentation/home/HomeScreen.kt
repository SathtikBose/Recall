package com.buildstack.recall.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildstack.recall.theme.ButtonGlow
import com.buildstack.recall.theme.White

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.buildstack.recall.presentation.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAdd: () -> Unit,
    onEditReminder: (Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val reminders = viewModel.reminders.collectAsStateWithLifecycle().value
    val categories = viewModel.categories.collectAsStateWithLifecycle().value
    val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recall", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = ButtonGlow,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (categories.isNotEmpty()) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        androidx.compose.material3.FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) },
                            label = { Text("All") },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ButtonGlow,
                                selectedLabelColor = White
                            )
                        )
                    }
                    items(categories, key = { it }) { category ->
                        androidx.compose.material3.FilterChip(
                            selected = selectedCategory == category,
                            onClick = { viewModel.selectCategory(category) },
                            label = { Text(category) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ButtonGlow,
                                selectedLabelColor = White
                            )
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (reminders.isEmpty()) {
                    // Empty State
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Empty State",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = if (selectedCategory != null) "No reminders for $selectedCategory" else "No Reminders Yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "Create your first reminder and stay organized.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reminders, key = { it.id }) { reminder ->
                            val dismissState = androidx.compose.material3.rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == androidx.compose.material3.SwipeToDismissBoxValue.EndToStart || 
                                        it == androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd) {
                                        viewModel.deleteReminder(reminder)
                                        true
                                    } else false
                                }
                            )

                            androidx.compose.material3.SwipeToDismissBox(
                                state = dismissState,
                                modifier = Modifier.animateItem(),
                                backgroundContent = {
                                    val color = if (dismissState.dismissDirection == androidx.compose.material3.SwipeToDismissBoxValue.EndToStart || 
                                                    dismissState.dismissDirection == androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd) 
                                                    Color.Red.copy(alpha = 0.8f) else Color.Transparent
                                    
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = if (dismissState.dismissDirection == androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = White
                                        )
                                    }
                                }
                            ) {
                                GlassCard(
                                    modifier = Modifier.clickable { onEditReminder(reminder.id) }
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                        Text(text = reminder.title, color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        if (reminder.description.isNotBlank()) {
                                            Text(text = reminder.description, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontSize = 14.sp)
                                        }
                                        Text(text = "${reminder.reminderDate} ${reminder.reminderTime}", color = White, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
