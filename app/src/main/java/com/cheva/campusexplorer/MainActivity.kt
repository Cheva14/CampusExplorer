package com.cheva.campusexplorer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val vm: MainActivityViewModel by viewModels()
    private val statisticsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.getIntExtra("data", 0)
            }
        }
    private val mapsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.getIntExtra("data", 0)
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
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        grantPerms()
        setContentView(R.layout.activity_main)

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val statisticsBtn = findViewById<Button>(R.id.statisticsBtn)
        val mapBtn = findViewById<Button>(R.id.mapButton)
        val recyclerView = findViewById<RecyclerView>(R.id.my_list)

//        val places = arrayOf(PlaceDetails("Lubbers Stadium", "The Grand Valley State University Laker football team enjoys the friendly confines of Arend D. Lubbers Stadium, one of the top Division II facilities in the nation.", isSelected = false, isFound = false),PlaceDetails("my home", "this is my home", isSelected = false, isFound = true))
        fun showDesc(place: PlaceDetails): Unit {
            vm.showDesc(place)
            println("You selected ${place.title}")
        }
        recyclerView.adapter = MyAdapter(vm.places.value!!, ::showDesc) // Adapter class in Step 3
        // Use single column layout
        recyclerView.layoutManager = LinearLayoutManager(this)

        vm.uid.observe(this) {
            it?.let {
                println("Your UID: $it")
            }
        }

        vm.places.observe(this) {
            recyclerView.adapter?.notifyDataSetChanged() // iOS: UITableView.reloadData()
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
    override fun onResume() {
        super.onResume()
        vm.setPlaceDetailsArrayList()
    }
}