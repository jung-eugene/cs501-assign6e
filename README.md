# CS 501 Individual Assignment 6 Question 5 — LocationInfo

## Explanation
The **LocationInfo** app demonstrates how to work with **location permissions**, **Google Maps**, and **reverse-geocoding** inside an Android Compose project.
Once the user grants location access, the app:

* Retrieves the user’s real GPS location (or emulator-simulated location)
* Centers the Google Map camera on that point
* Places a **“You are here”** marker
* Converts coordinates into a readable **street address**
* Allows users to **add custom markers** anywhere on the map by tapping

The app uses **FusedLocationProviderClient** for continuous location updates and **Google Maps Compose** for rendering the map UI.

## How to Use
1. Launch the app.
2. When prompted, **grant location permission**.
3. If using an emulator, open **Extended Controls → Location** and set a custom latitude/longitude.
   * The map will automatically move and update the address.
4. Tap anywhere on the map to **place a custom marker**.
5. Move your simulated location again to see the marker and address update in real time.

## Implementation

### Location Permission
The app requests `ACCESS_FINE_LOCATION` using `rememberLauncherForActivityResult`.

### GPS & Address Retrieval
* Uses **FusedLocationProviderClient** to read location updates every 2 seconds.
* Converts coordinates to a human-readable address using `Geocoder`.

### Google Maps Integration
The map uses `GoogleMap`, `Marker`, and `CameraPositionState` from the **Maps Compose** library.

### User Features
* Blue dot = system “My Location” indicator
* Red marker = user’s actual location
* Additional markers = appear wherever the user taps
* Map auto-recenters when the device/emulator location changes

### State Management
* `mutableStateOf` stores location & address
* `mutableStateListOf` stores custom markers
* `LaunchedEffect` triggers camera movement and geocoding updates

## Notes on Setup (API Key Required)
To use Google Maps, you must add a **Google Maps API Key**:

1. Go to **Google Cloud Console**
2. Create a project
3. Enable **Maps SDK for Android**
4. Create an **Android API key**
5. Add your app’s SHA-1 + package name
6. Put the key in your `local.properties`:

```
MAPS_API_KEY=YOUR_KEY_HERE
```

Android Studio injects it into the manifest automatically.
