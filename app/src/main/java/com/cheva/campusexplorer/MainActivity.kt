package com.cheva.campusexplorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cheva.campusexplorer.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val vm: MainActivityViewModel by viewModels()
    private val statisticsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.getIntExtra("data", 0)
                println("stats sent: $data")
            }
        }
    private val mapsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.getIntExtra("data", 0)
                println("maps sent: $data")
            }
        }

    private fun grantPerms() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    println("Precise location access granted.")
                }
                permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    println("Only approximate location access granted.")
                } else -> {
                // No location access granted.
                println("No location access granted.")
            }
            }
        }

        // ...

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION))
        println("$locationPermissionRequest")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        grantPerms()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_main)
//        setContentView(binding.root)

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val statisticsBtn = findViewById<Button>(R.id.statisticsBtn)
        val mapBtn = findViewById<Button>(R.id.mapButton)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        vm.uid.observe(this) {
            it?.let {
                println("Your UID: $it")
            }
        }
        logoutBtn.setOnClickListener {
            finish()
            vm.logout()
        }
        statisticsBtn.setOnClickListener {


            val toStatistics = Intent(this, StatisticsActivity::class.java)
            toStatistics.putExtra("data", 0)
            statisticsLauncher.launch(toStatistics)
        }
        mapBtn.setOnClickListener {
            val toMaps = Intent(this, MapsActivity::class.java)
            toMaps.putExtra("data", 0)
            mapsLauncher.launch(toMaps)

        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
//    override fun onResume() {
//        super.onResume()
//        print("on resume, maybe fetch any new data")
//    }
}