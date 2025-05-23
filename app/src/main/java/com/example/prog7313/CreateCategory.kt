package com.example.prog7313

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.prog7313.R.anim.slide_in_right
import com.example.prog7313.R.anim.slide_out_left
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateCategory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_category)

        //--------------------------------------------
        // Bottom navigation bar
        //--------------------------------------------

        setupNavigation()

        //--------------------------------------------
        // Get references
        //--------------------------------------------

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val editCategoryName = findViewById<EditText>(R.id.editCategoryName)
        val btnCreateCategory = findViewById<Button>(R.id.btnCreateCategory)

        //--------------------------------------------
        // Handle category logic
        //--------------------------------------------

        btnCreateCategory.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val type = if (selectedId == R.id.radioIncome) "Income" else "Expense"
            val name = editCategoryName.text.toString()

            if (name.isNotBlank()) {
                val category = UserCategoryData(name = name, transactionType = type)
                lifecycleScope.launch {
                    AppDatabase.getDatabase(applicationContext).userCategoryDao().insert(category)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreateCategory, "Category Created!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Enter a category name", Toast.LENGTH_SHORT).show()
            }
        }

        val btnEditCategory = findViewById<Button>(R.id.btnEditCategories)

        btnEditCategory.setOnClickListener {
            val intent = Intent(this, EditCategories::class.java)
            startActivity(intent)
        }
    }

    //--------------------------------------------
    // Bottom nav bar navigation
    //--------------------------------------------

    private fun setupNavigation() {

        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTimeline = findViewById<LinearLayout>(R.id.navTimeline)
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)

        //--------------------------------------------
        // Click listeners
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