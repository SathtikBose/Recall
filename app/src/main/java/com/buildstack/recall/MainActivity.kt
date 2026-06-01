package com.buildstack.recall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.buildstack.recall.theme.RecallTheme
import dagger.hilt.android.AndroidEntryPoint

import android.os.Build
import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.buildstack.recall.presentation.AppNavigation
import com.buildstack.recall.presentation.receiver.AlarmService

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      val launcher = rememberLauncherForActivityResult(
          ActivityResultContracts.RequestPermission()
      ) { isGranted: Boolean ->
          // Handle permission grant
      }
      
      LaunchedEffect(Unit) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
      }

      val lifecycleOwner = LocalLifecycleOwner.current
      DisposableEffect(lifecycleOwner) {
          val observer = LifecycleEventObserver { _, event ->
              if (event == Lifecycle.Event.ON_RESUME) {
                  val stopIntent = Intent(this@MainActivity, AlarmService::class.java).apply {
                      action = AlarmService.ACTION_STOP
                  }
                  startService(stopIntent)
              }
          }
          lifecycleOwner.lifecycle.addObserver(observer)
          onDispose {
              lifecycleOwner.lifecycle.removeObserver(observer)
          }
      }

      RecallTheme { Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { AppNavigation() } }
    }
  }
}
