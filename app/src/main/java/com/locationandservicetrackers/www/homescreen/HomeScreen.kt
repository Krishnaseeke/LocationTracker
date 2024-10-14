package com.locationandservicetrackers.www.homescreen



import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(context: Context, modifier: Modifier = Modifier) {
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        if (!locationPermissionState.allPermissionsGranted) {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var isLocationUpdatesEnabled by remember { mutableStateOf(false) }
    val locationList = remember { mutableStateListOf<Location>() }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startLocationUpdates(fusedLocationClient, context) { location ->
                locationList.add(location)
            }
            isLocationUpdatesEnabled = true
        } else {
            Toast.makeText(context, "Location Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates(fusedLocationClient, context) { location ->
                locationList.add(location)
            }
            isLocationUpdatesEnabled = true
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Text(
            text = "Location is Capturing",
            color = Color.Black,
            fontSize = 16.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold
        )

        LazyColumn {
            items(locationList) { location ->
                Row {
                    Text(text = "Lat: ${location.latitude}", modifier = Modifier.weight(1f))
                    Text(text = "Lng: ${location.longitude}", modifier = Modifier.weight(1f))
                }
            }
        }

        // Button to start background location service
        androidx.compose.material3.Button(onClick = {
            val intent = Intent(context, LocationService::class.java)
            intent.putExtra("name", "Location Tracker Service")
            ContextCompat.startForegroundService(context, intent)
        }) {
            Text(text = "Start Location Service")
        }
    }
}


// Function to start capturing location updates every 10 seconds
fun startLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationReceived: (Location) -> Unit
) {
    val locationRequest = LocationRequest.create().apply {
        interval = 10_000 // 10 seconds
        fastestInterval = 5_000 // 5 seconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations) {
                onLocationReceived(location)
            }
        }
    }

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
}
