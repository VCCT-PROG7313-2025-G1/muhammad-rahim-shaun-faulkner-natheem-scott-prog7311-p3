package com.example.prog7313

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7313.R.anim.slide_in_right
import com.example.prog7313.R.anim.slide_out_left
import kotlinx.coroutines.launch

class EditCategories : AppCompatActivity() {

    //--------------------------------------------
    // Viewmodel variables
    //--------------------------------------------

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserCategoryAdapter
    private lateinit var viewModel: UserCategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_categories)

        //--------------------------------------------
        // Initialized recycler view and adapter
        // https://stackoverflow.com/questions/71604788/why-my-recyclerview-show-unresolved-reference-recyclerview
        //--------------------------------------------

        recyclerView = findViewById(R.id.rvUserCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UserCategoryAdapter(emptyList()) { category ->
            deleteCategory(category)
        }

        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this).get(UserCategoryViewModel::class.java)

        viewModel.categories.observe(this) { categoryList ->
            adapter.updateData(categoryList)
        }

        viewModel.loadAllCategories()
    }

    //--------------------------------------------
    // Delete user category function
    //--------------------------------------------

    private fun deleteCategory(category: UserCategoryData) {
        viewModel.viewModelScope.launch {
            AppDatabase.getDatabase(application).userCategoryDao().delete(category)
            viewModel.loadCategories(category.transactionType)
        }
    }

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