package com.example.prog7313

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AccountListAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val accounts = mutableListOf<AccountData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_list)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Accounts"

        DrawerHelper.setupDrawer(this, drawerLayout, toolbar, navView)

        recyclerView = findViewById(R.id.recyclerViewAccounts)

        adapter = AccountListAdapter(accounts,
            onEditClick = { account -> editAccount(account) },
            onDeleteClick = { account -> confirmDelete(account) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadAccountsFromFirestore()
    }

    private fun loadAccountsFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        println("Loading accounts for userId = $userId")

        db.collection("users")
            .document(userId)
            .collection("accounts")
            .get()
            .addOnSuccessListener { result ->
                println("Fetched ${result.size()} documents")
                accounts.clear()
                for (document in result) {
                    println("Doc: ${document.data}")
                    val account = document.toObject(AccountData::class.java).copy(id = document.id)
                    accounts.add(account)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load accounts", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editAccount(account: AccountData) {
        val intent = Intent(this, EditAccountActivity::class.java)
        intent.putExtra("accountId", account.id)
        startActivity(intent)
    }

    private fun confirmDelete(account: AccountData) {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete this account?")
            .setPositiveButton("Delete") { _, _ -> deleteAccount(account) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAccount(account: AccountData) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).collection("accounts").document(account.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                loadAccountsFromFirestore()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        loadAccountsFromFirestore() // Refresh list after returning from edit
    }

}