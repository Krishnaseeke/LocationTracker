package com.locationandservicetrackers.www.utilities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.locationandservicetrackers.www.homescreen.HomeScreen


import com.locationandservicetrackers.www.ui.theme.LocationTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocationTrackerTheme {
                // Pass context to HomeScreen
                val context = this
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Apply padding to HomeScreen to prevent UI overlap issues
                    HomeScreen(
                        context = context,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
