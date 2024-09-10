package Commuter

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.secureride.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.example.secureride.databinding.ActivityMapsBinding

class CommuterDashboard : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentLocationMarker: Marker? = null
    private var pinnedLocationMarker: Marker? = null
    private var routePolyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Find the button and set its click listener
        val checkAvailabilityButton: Button = findViewById(R.id.check_availability_button)
        checkAvailabilityButton.setOnClickListener {
            checkAvailability()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        // Enable user location on the map
        mMap.isMyLocationEnabled = true

        // Set up real-time location updates
        setupLocationUpdates()

        // Set map click listener to add a pin on the map when user taps on the map
        mMap.setOnMapClickListener { latLng ->
            // Remove previous pinned marker
            pinnedLocationMarker?.remove()

            // Add marker to the clicked location
            pinnedLocationMarker = mMap.addMarker(
                MarkerOptions().position(latLng).title("Pinned Location")
            )

            // Redraw polyline between the current location and the pinned location
            currentLocationMarker?.let { currentMarker ->
                val currentLatLng = currentMarker.position
                drawPolyline(currentLatLng, latLng)
            }
        }
    }

    private fun setupLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Update location every 5 seconds
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateCurrentLocation(location)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

    private fun updateCurrentLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)

        // Remove the old marker
        currentLocationMarker?.remove()

        // Add a new marker for the current location
        currentLocationMarker = mMap.addMarker(
            MarkerOptions().position(currentLatLng).title("Current Location")
        )

        // Move the camera to the current location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14.0f))

        // Redraw polyline if pinned location exists
        pinnedLocationMarker?.let { pinnedMarker ->
            drawPolyline(currentLatLng, pinnedMarker.position)
        }
    }

    private fun drawPolyline(startLatLng: LatLng, endLatLng: LatLng) {
        // Remove old polyline if it exists
        routePolyline?.remove()

        // Draw a polyline between current location and pinned location
        val polylineOptions = PolylineOptions()
            .add(startLatLng, endLatLng)
            .color(0xFF0000FF.toInt()) // Set color (blue)
            .width(10f) // Set width

        routePolyline = mMap.addPolyline(polylineOptions)
    }

    private fun checkAvailability() {
        // This is where you'd implement functionality to check nearby places or availability
        // For example, you might use an API to search for nearby places or services
        // Here's a placeholder for now
        pinnedLocationMarker?.let {
            val pinnedLatLng = it.position
            // Do something with the pinned location, such as finding nearby places
            // You can add additional logic here as needed
            println("Checking availability near: $pinnedLatLng")
        } ?: run {
            // If no pinned location is set, show a message or handle accordingly
            println("No pinned location set.")
        }
    }
}
