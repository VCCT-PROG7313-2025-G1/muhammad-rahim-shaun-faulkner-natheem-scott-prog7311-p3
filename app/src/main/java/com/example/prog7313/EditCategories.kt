package com.example.prog7313

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditCategories : AppCompatActivity() {

    //--------------------------------------------
    // Viewmodel variables
    //--------------------------------------------

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserCategoryAdapter
    private lateinit var viewModel: UserCategoryViewModel

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_categories)

        //--------------------------------------------
        // Burger menu logic
        //--------------------------------------------

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Custom Categories"

        DrawerHelper.setupDrawer(this, drawerLayout, toolbar, navView)

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
        if (currentUserId.isNotEmpty() && category.id.isNotEmpty()) {
            db.collection("users")
                .document(currentUserId)
                .collection("categories")
                .document(category.id)
                .delete()
                .addOnSuccessListener {
                    viewModel.loadAllCategories()
                }
        }
    }
}
