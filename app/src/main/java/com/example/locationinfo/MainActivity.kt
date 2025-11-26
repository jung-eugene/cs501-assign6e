package com.example.locationinfo

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.locationinfo.ui.theme.LocationinfoTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationinfoTheme {
                MapScreen()
            }
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current

    var permissionGranted by remember { mutableStateOf(false) }
    var userLat by remember { mutableStateOf(42.3601) }
    var userLng by remember { mutableStateOf(-71.0589) }
    var address by remember { mutableStateOf("Waiting for location…") }

    // Use a state list for markers (easier + reliable)
    val customMarkers = remember { mutableStateListOf<LatLng>() }

    // Permission launcher
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Location callback
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                userLat = loc.latitude
                userLng = loc.longitude

                val geocoder = Geocoder(context, Locale.getDefault())
                val addr = geocoder.getFromLocation(userLat, userLng, 1)
                address = addr?.firstOrNull()?.getAddressLine(0) ?: "Address unavailable"
            }
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 2000L
            ).build()

            fusedClient.requestLocationUpdates(
                request,
                locationCallback,
                context.mainLooper
            )
        }
    }

    val cameraPositionState = rememberCameraPositionState()

    // Recenter camera when user location changes
    LaunchedEffect(userLat, userLng) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            LatLng(userLat, userLng),
            15f
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("LocationInfo", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            Text(
                text = "Your Address:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )

            Text(
                text = address,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(12.dp))

            // The map takes the rest of the screen and is fully tappable
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = permissionGranted
                ),
                onMapClick = { latLng ->
                    // Add a new marker where user tapped
                    customMarkers.add(latLng)
                }
            ) {
                // Marker at user location
                Marker(
                    state = MarkerState(position = LatLng(userLat, userLng)),
                    title = "You are here"
                )

                // Draw all custom markers
                customMarkers.forEachIndexed { index, pos ->
                    Marker(
                        state = MarkerState(position = pos),
                        title = "Custom Marker ${index + 1}"
                    )
                }
            }
        }
    }
}

