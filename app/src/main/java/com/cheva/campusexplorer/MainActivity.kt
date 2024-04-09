package com.cheva.campusexplorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val statisticsBtn = findViewById<Button>(R.id.statisticsBtn)
        val mapBtn = findViewById<Button>(R.id.mapButton)

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
//    override fun onResume() {
//        super.onResume()
//        print("on resume, maybe fetch any new data")
//    }
}