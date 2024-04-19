package com.cheva.campusexplorer

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.cheva.campusexplorer.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val mapVM: MapsViewModel by viewModels()

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mCurrentLocation: Location? = null
    private lateinit var locationCallback: LocationCallback

    private var requestingLocationUpdates = false

    // Define a variable to hold the Places API key.
    private val apiKey = BuildConfig.MAPS_API_KEY

    // Use fields to define the data types to return.
    private val placeFields: List<Place.Field> = listOf(Place.Field.NAME)

    // Use the builder to create a FindCurrentPlaceRequest.
    val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

    private var locationOverlayView: View? = null

    // Variable to hold the timestamp of the last Snackbar display
    private var lastSnackbarDisplayTime: Long = 0

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inflate the location overlay view
        locationOverlayView = layoutInflater.inflate(R.layout.layout_location_overlay, null)

        // Get the MapView from the layout
        val mapView = mapFragment.view

        // Check if the MapView is not null and is an instance of ViewGroup
        if (mapView != null && mapView is ViewGroup) {
            // Add the overlay view to the MapView
            mapView.addView(locationOverlayView)
        } else {
            // Handle the case where the MapView is null or not a ViewGroup
            Log.e(TAG, "MapView is null or not a ViewGroup")
        }

        // Log an error if apiKey is not set.
        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            println("Places test, No api key")
            finish()
            return
        }

        // Initialize the SDK
        Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        val tmp = this

        // Inside your activity or fragment
        mapVM.locations.observe(this) { locations ->
            // Perform UI updates or other actions using the `locations` list
            addCircles(locations)
        }
        mapVM.placesFound.observe(this) { placesFound ->
            addMarkers(placesFound)
        }

        locationCallback = object : LocationCallback() {
            val addedCirclePositions = mutableListOf<LatLng>()
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations){
                    val roundedLat = "%.4f".format(location.latitude).toDouble()
                    val roundedLng = "%.4f".format(location.longitude).toDouble()
                    val latLng = LatLng(roundedLat, roundedLng)
                    if (!addedCirclePositions.contains(latLng)) {
                        val circleOptions = CircleOptions()
                            .center(latLng)
                            .radius(100.0) // In meters
                            .strokeColor(Color.argb(100, 0, 255, 255))
                            .fillColor(Color.argb(100, 0, 255, 255))

                        mapVM.addLocationToFirestore(roundedLat, roundedLng)

                        val circle = mMap.addCircle(circleOptions)

                        // Add the position to the list of added positions
                        addedCirclePositions.add(latLng)
                    }

                }

                // Call findCurrentPlace and handle the response (first check that the user has granted permission).
                if (ContextCompat.checkSelfPermission(tmp, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                    val placeResponse = placesClient.findCurrentPlace(request)
                    placeResponse.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val response = task.result

                            for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
                                val locationName = placeLikelihood.place.name
                                val locationLikelihood = placeLikelihood.likelihood

                                if (locationName == "Lubbers Stadium" && locationLikelihood > 0.4) {
                                    showLocationSnackbar(locationName)
                                } else if (locationName == "Recreation Center" && locationLikelihood > 0.3) {
                                    showLocationSnackbar(locationName)
                                } else if (locationName == "GVSU Laker Store" && locationLikelihood > 0.1) {
                                    showLocationSnackbar(locationName)
                                } else if (locationName == "Cook Carillon Tower (CCT)" && locationLikelihood > 0.1) {
                                    showLocationSnackbar(locationName)
                                } else if (locationName == "Mary Idema Pew Library (LIB)" && locationLikelihood > 0.08) {
                                    showLocationSnackbar(locationName)
                                }
                            }
                        } else {
                            val exception = task.exception
                            if (exception is ApiException) {
                                Log.e(TAG, "Place not found: ${exception.statusCode}")
                            }
                        }
                    }
                } else {
                    // A local method to request required permissions;
                    // See https://developer.android.com/training/permissions/requesting
//            getLocationPermission()
                }
            }
        }
    }

    // Function to show the Snackbar
    private fun showLocationSnackbar(locationName: String) {
        if (mapVM.placesFound.value?.get(locationName) == false) {
            val snackbar = Snackbar.make(binding.root, "Congratulations! You found the $locationName!", Snackbar.ANIMATION_MODE_SLIDE)
            snackbar.show()
            mapVM.updateFirestore(locationName)
            mapVM.updatePlacesFound(locationName)
            addMarker(locationName)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val data = intent.getIntExtra("data", 0)
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        println("Stopping Location Updates")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun startLocationUpdates() {
        println("starting Location Updates")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                mCurrentLocation = location
                // Add a marker in location and move the camera
                val myLoc = LatLng(location?.latitude!!, location.longitude)
                val zoomLevel = 15f // Adjust the zoom level as per your requirement
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, zoomLevel))            }
        createLocationRequest()
        startLocationUpdates()
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this@MapsActivity,
                        0)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun addCircles(locations:  List<GeoPoint>) {
        for (location in locations){
            val lat = location.latitude
            val lng = location.longitude
            val latLng = LatLng(lat, lng)

            val circleOptions = CircleOptions()
                .center(latLng)
                .radius(100.0) // In meters
                .strokeColor(Color.argb(100, 0, 255, 255))
                .fillColor(Color.argb(100, 0, 255, 255))

            mapVM.addLocationToFirestore(lat, lng)

            val circle = mMap.addCircle(circleOptions)
        }
    }

    private fun addMarkers(placesFound: HashMap<String, Boolean>) {
        val library = LatLng(42.9630, -85.8902)
        val tower = LatLng(42.9635, -85.8886)
        val store = LatLng(42.9652, -85.8895)
        val rec = LatLng(42.9669, -85.8898)
        val stadium = LatLng(42.9690, -85.8947)
        if (placesFound["Mary Idema Pew Library (LIB)"] == true) {
            mMap.addMarker(
                MarkerOptions()
                    .position(library)
                    .title("Mary Idema Pew Library (LIB)")
            )
        }
        if (placesFound["Cook Carillon Tower (CCT)"] == true) {

            mMap.addMarker(
                MarkerOptions()
                    .position(tower)
                    .title("Cook Carillon Tower (CCT)")
            )}
        if (placesFound["GVSU Laker Store"] == true) {

            mMap.addMarker(
                MarkerOptions()
                    .position(store)
                    .title("GVSU Laker Store")
            )}
        if (placesFound["Recreation Center"] == true) {
            mMap.addMarker(
                MarkerOptions()
                    .position(rec)
                    .title("Recreation Center")
            )
        }
        if (placesFound["Lubbers Stadium"] == true) {

            mMap.addMarker(
                MarkerOptions()
                    .position(stadium)
                    .title("Lubbers Stadium")

            )
        }
    }
    private fun addMarker(locationName: String) {
        val library = LatLng(42.9630, -85.8902)
        val tower = LatLng(42.9635, -85.8886)
        val store = LatLng(42.9652, -85.8895)
        val rec = LatLng(42.9669, -85.8898)
        val stadium = LatLng(42.9690, -85.8947)
        if ("Mary Idema Pew Library (LIB)" == locationName) {
            mMap.addMarker(
                MarkerOptions()
                    .position(library)
                    .title("Mary Idema Pew Library (LIB)")
            )
        }
        if ("Cook Carillon Tower (CCT)" == locationName) {

            mMap.addMarker(
                MarkerOptions()
                    .position(tower)
                    .title("Cook Carillon Tower (CCT)")
            )}
        if ("GVSU Laker Store" == locationName) {

            mMap.addMarker(
                MarkerOptions()
                    .position(store)
                    .title("GVSU Laker Store")
            )}
        if ("Recreation Center" == locationName) {
            mMap.addMarker(
                MarkerOptions()
                    .position(rec)
                    .title("Recreation Center")
            )
        }
        if ("Lubbers Stadium" == locationName) {

            mMap.addMarker(
                MarkerOptions()
                    .position(stadium)
                    .title("Lubbers Stadium")

            )
        }
    }
}