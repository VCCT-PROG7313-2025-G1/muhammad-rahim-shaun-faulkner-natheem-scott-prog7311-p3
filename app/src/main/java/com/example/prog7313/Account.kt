package com.example.prog7313

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.google.firebase.firestore.Query

class Account : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        //--------------------------------------------
        // drawer layout setup for burger menu
        //--------------------------------------------

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Profile"

        DrawerHelper.setupDrawer(this, drawerLayout, toolbar, navView)

        //--------------------------------------------
        // UI bindings and on click listeners
        //--------------------------------------------

        val btnAddAccount = findViewById<ImageView>(R.id.iv_addAccount)
        val btnCreateCategory = findViewById<ImageView>(R.id.iv_createCategory)

        btnAddAccount.setOnClickListener {
            val intent = Intent(this, AddBankAccount::class.java)
            startActivity(intent)
        }

        btnCreateCategory.setOnClickListener {
            val intent = Intent(this, CreateCategory::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //--------------------------------------------
        // Firebase initialization
        //--------------------------------------------

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvFullName = findViewById<TextView>(R.id.tvFullName)

        //--------------------------------------------
        // Checks if user is logged in
        //--------------------------------------------

        currentUser?.uid?.let { uid ->
            val userDocRef = firestore.collection("users").document(uid)
            userDocRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("userName") ?: "Unknown User"
                    val fullName = document.getString("fullName") ?: "Unknown Name"

                    tvUsername.text = username
                    tvFullName.text = fullName

                    loadUserAccounts(uid)
                    loadLatestTransactions(uid)
                } else {
                    tvUsername.text = "Username not found"
                    tvFullName.text = "Full Name not found"
                }
            }.addOnFailureListener { exception ->
                tvUsername.text = "Error"
                tvFullName.text = "Error"
            }
        }
    }

    //--------------------------------------------
    // Function to load user bank accounts
    //--------------------------------------------

    private fun loadUserAccounts(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val containerAccounts = findViewById<LinearLayout>(R.id.containerAccounts)

        firestore.collection("users")
            .document(userId)
            .collection("accounts")
            .get()
            .addOnSuccessListener { querySnapshot ->
                containerAccounts.removeAllViews()

                for (document in querySnapshot.documents) {
                    val bankName = document.getString("bankName") ?: "No Bank Name"
                    val accountType = document.getString("accountType") ?: "No Account Type"
                    val balance = document.getDouble("balance") ?: 0.0

                    val accountView = LayoutInflater.from(this)
                        .inflate(R.layout.item_account_card, containerAccounts, false)

                    val tvBankName = accountView.findViewById<TextView>(R.id.tvBankName)
                    val tvAccountType = accountView.findViewById<TextView>(R.id.tvAccountType)
                    val tvBalance = accountView.findViewById<TextView>(R.id.tvBalance)

                    tvBankName.text = bankName
                    tvAccountType.text = accountType
                    tvBalance.text = String.format("R %.2f", balance)

                    containerAccounts.addView(accountView)
                }
            }
    }

    //--------------------------------------------
    // Function to load users latest transactions
    //--------------------------------------------

    private fun loadLatestTransactions(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val containerLatestTransactions = findViewById<LinearLayout>(R.id.containerLatestTransactions)

        firestore.collection("users")
            .document(userId)
            .collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(2)
            .get()
            .addOnSuccessListener { querySnapshot ->
                containerLatestTransactions.removeAllViews()

                for (document in querySnapshot.documents) {
                    val transactionName = document.getString("category") ?: "Unknown"
                    val amount = document.getDouble("amount") ?: 0.0
                    val amountText = String.format("R %.2f", amount)

                    val transactionView = LayoutInflater.from(this)
                        .inflate(R.layout.item_user_transaction, containerLatestTransactions, false)

                    val tvName = transactionView.findViewById<TextView>(R.id.tvCustomName)
                    val tvAmount = transactionView.findViewById<TextView>(R.id.tvTransactionAmount)
                    val tvViewDetails = transactionView.findViewById<TextView>(R.id.tvViewDetails)
                    val ivCategoryIcon = transactionView.findViewById<ImageView>(R.id.ivCategoryIcon)

                    val transactionType = document.getString("transactionType") ?: "Income"

                    ivCategoryIcon.setImageResource(
                        if (transactionType == "Expense") R.drawable.ic_minus
                        else R.drawable.ic_plus
                    )

                    tvName.text = transactionName
                    tvAmount.text = amountText
                    tvAmount.setTextColor(if (transactionType == "Expense") Color.RED else Color.GREEN)

                    tvViewDetails.setOnClickListener {
                        val intent = Intent(this, IndividualTransactions::class.java)
                        intent.putExtra("transactionId", document.id)
                        startActivity(intent)
                    }

                    containerLatestTransactions.addView(transactionView)
                }
            }
    }
}