package com.example.prog7313

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prog7313.R.anim.slide_in_right
import com.example.prog7313.R.anim.slide_out_left
import org.w3c.dom.Text

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        setupNavigation()

        //--------------------------------------------
        // UI binds for click listeners
        //--------------------------------------------

        val settingHome = findViewById<TextView>(R.id.settingHome)
        val settingTimeline = findViewById<TextView>(R.id.settingTimeline)
        val tvCreateCategory = findViewById<TextView>(R.id.settingCreateCategory)
        val logOut = findViewById<TextView>(R.id.tvLogout)

        settingHome.setOnClickListener {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }

        settingTimeline.setOnClickListener {
            val intent = Intent(this, Timeline::class.java)
            startActivity(intent)
        }

        tvCreateCategory.setOnClickListener {
            val intent = Intent(this, CreateCategory::class.java)
            startActivity(intent)
        }

        logOut.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }

    //--------------------------------------------
    //
    //--------------------------------------------

    private fun setupNavigation() {

        //--------------------------------------------
        //
        //--------------------------------------------

        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTimeline = findViewById<LinearLayout>(R.id.navTimeline)

        //--------------------------------------------
        //
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
    }
}