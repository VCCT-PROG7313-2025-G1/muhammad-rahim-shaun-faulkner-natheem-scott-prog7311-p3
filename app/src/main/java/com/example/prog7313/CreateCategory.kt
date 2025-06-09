package com.example.prog7313

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateCategory : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var editCategoryName: EditText
    private lateinit var btnCreateCategory: Button
    private lateinit var btnEditCategory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_category)

        //--------------------------------------------
        // Burger menu setup
        //--------------------------------------------

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Create Category"

        DrawerHelper.setupDrawer(this, drawerLayout, toolbar, navView)

        //--------------------------------------------
        // Get references
        //--------------------------------------------

        radioGroup = findViewById(R.id.radioGroup)
        editCategoryName = findViewById(R.id.editCategoryName)
        btnCreateCategory = findViewById(R.id.btnCreateCategory)
        btnEditCategory = findViewById(R.id.btnEditCategories)

        //--------------------------------------------
        // Handle category logic
        //--------------------------------------------

        btnCreateCategory.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val type = if (selectedId == R.id.radioIncome) "Income" else "Expense"
            val name = editCategoryName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter a category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = hashMapOf(
                "name" to name,
                "transactionType" to type
            )

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)
                .collection("categories")
                .add(category)
                .addOnSuccessListener {
                    Toast.makeText(this, "Category Created!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to create category: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        btnEditCategory.setOnClickListener {
            val intent = Intent(this, EditCategories::class.java)
            startActivity(intent)
        }
    }
}