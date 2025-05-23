package com.example.prog7313

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prog7313.R.anim.slide_in_right
import com.example.prog7313.R.anim.slide_out_left

class Analytics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_analytics)

        //--------------------------------------------
        // Bottom nav bar setup
        //--------------------------------------------

        setupNavigation()
    }

    private fun setupNavigation() {

        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTimeline = findViewById<LinearLayout>(R.id.navTimeline)
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)

        //--------------------------------------------
        // Click listeners for nav bar
        //--------------------------------------------

        navHome.setOnClickListener {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
            // https://www.geeksforgeeks.org/how-to-add-slide-animation-between-activities-in-android/
            overridePendingTransition(slide_in_right, slide_out_left)
        }

        navTimeline.setOnClickListener {
            // Navigate to Timeline Activity
            val intent = Intent(this, Timeline::class.java)
            startActivity(intent)
            // https://www.geeksforgeeks.org/how-to-add-slide-animation-between-activities-in-android/
            overridePendingTransition(slide_in_right, slide_out_left)
        }

        navSettings.setOnClickListener {
            // Navigate to Settings Activity
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
            // https://www.geeksforgeeks.org/how-to-add-slide-animation-between-activities-in-android/
            overridePendingTransition(slide_in_right, slide_out_left)
        }
    }
}