package com.cheva.campusexplorer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
//import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class StatisticsActivity : AppCompatActivity() {
//    private val vm: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        findViewById<Button>(R.id.confirmBtn).setOnClickListener {
            val confirmIntent = Intent()
            confirmIntent.putExtra("data", 0)
            setResult(RESULT_OK, confirmIntent)
            finish()
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val data = intent.getIntExtra("data", 0)
        findViewById<TextView>(R.id.statisticsLabel).text = "Data is: $data"

    }
}