package com.example.prog7313

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ArrayAdapter
import android.widget.Spinner


class Transactions : AppCompatActivity() {

    //--------------------------------------------
    // private variables
    //--------------------------------------------

    private lateinit var firestoreRepo: FirestoreRepo

    private lateinit var spinnerAccounts: Spinner
    private val accountList = mutableListOf<Account>()

    private lateinit var textViewSelectCategory: TextView
    private var selectedCategory: String = ""

    data class Account(
        val accountId: String,
        val bankName: String,
        val accountType: String
    ) {
        override fun toString(): String {
            return "$bankName - $accountType"
        }
    }

    //--------------------------------------------
    // private variables for images
    //--------------------------------------------

    private lateinit var uploadPhotoButton: TextView
    private var selectedImageUri: Uri? = null

    //--------------------------------------------
    // private variables for frequency and timestamps
    //--------------------------------------------

    private var selectedFrequency: String? = null
    private var startTimestamp: Long? = null
    private var endTimestamp: Long? = null
    private var isRecurring = false

    //--------------------------------------------
    // Activity to open reoccurring activity
    //--------------------------------------------

    private val recurringActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            selectedFrequency = data?.getStringExtra("frequency")
            startTimestamp = data?.getLongExtra("startTimestamp", -1L)?.takeIf { it > 0}
            endTimestamp = data?.getLongExtra("endTimestamp", -1L)?.takeIf { it > 0}
        }
    }

    //--------------------------------------------
    // function to start category selection activity
    //--------------------------------------------

    private val categoryActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            selectedCategory = data?.getStringExtra("selectedCategory") ?: ""
            textViewSelectCategory.text = selectedCategory
        }
    }

    //--------------------------------------------
    // function to start photo selection activity
    //--------------------------------------------

    private val photoActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            data?.getStringExtra("selectedImageUri")?.let { imageUrlString ->
                selectedImageUri = Uri.parse(imageUrlString)
                uploadPhotoButton.text = "Photo Selected"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transactions)

        //--------------------------------------------
        // UI binds
        //--------------------------------------------

        val radioGroupTransactionType = findViewById<RadioGroup>(R.id.transactionTypeGroup)
        val radioIncome = findViewById<RadioButton>(R.id.rbIncome)
        val radioExpense = findViewById<RadioButton>(R.id.rbExpense)
        val editTextAmount = findViewById<EditText>(R.id.etAmount)
        val editTextNotes = findViewById<EditText>(R.id.etNotes)
        val buttonSubmit = findViewById<Button>(R.id.btnSubmit)
        textViewSelectCategory = findViewById(R.id.tvSelectCategory)
        uploadPhotoButton = findViewById(R.id.tvUploadPhoto)
        val tvRecurring = findViewById<TextView>(R.id.tvRecurring)
        spinnerAccounts = findViewById(R.id.spinnerAccounts)

        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        firestoreRepo = FirestoreRepo()

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("accounts")
            .get()
            .addOnSuccessListener { querySnapShot ->
                accountList.clear()
                for (doc in querySnapShot.documents) {
                    val accountId = doc.id
                    val bankName = doc.getString("bankName") ?: "Unknown Bank"
                    val accountType = doc.getString("accountType") ?: "Unknown Type"
                    accountList.add(Account(accountId, bankName, accountType))
                }

                if (accountList.isEmpty()) {
                    Toast.makeText(this, "No accounts found! Please add an account first.", Toast.LENGTH_LONG).show()
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountList)
                adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
                spinnerAccounts.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load accounts: ${e.message}", Toast.LENGTH_LONG).show()
            }

        //--------------------------------------------
        // Function to select category
        //--------------------------------------------

        textViewSelectCategory.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            val transactionType = when {
                radioIncome.isChecked -> "Income"
                radioExpense.isChecked -> "Expense"
                else -> {
                    Toast.makeText(this, "Please Select Income or Expense.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            intent.putExtra("transactionType", transactionType)
            categoryActivityLauncher.launch(intent)
        }

        //--------------------------------------------
        // upload photo click listener
        //--------------------------------------------

        uploadPhotoButton.setOnClickListener {
            val intent = Intent(this, UploadPhoto::class.java)
            photoActivityLauncher.launch(intent)
        }

        //--------------------------------------------
        // recurring activity click listener
        //--------------------------------------------

        tvRecurring.setOnClickListener {
            isRecurring = !isRecurring
            tvRecurring.setBackgroundResource(
                if (isRecurring) R.drawable.button_rounded_selected else R.drawable.button_rounded_dark
            )

            if (isRecurring) {
                val intent = Intent(this, Recurring::class.java)
                recurringActivityLauncher.launch(intent)
            } else {
                selectedFrequency = null
                startTimestamp = null
                endTimestamp = null
            }
        }

        //--------------------------------------------
        // Submit click listener
        //--------------------------------------------

        buttonSubmit.setOnClickListener {
            val transactionType = when {
                radioIncome.isChecked -> "Income"
                radioExpense.isChecked -> "Expense"
                else -> ""
            }

            if (transactionType.isEmpty()) {
                Toast.makeText(this, "Please select a transaction type!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedCategory.isEmpty()) {
                Toast.makeText(this, "Please select a category!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = editTextAmount.text.toString().toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRecurring) {
                if (startTimestamp == null || endTimestamp == null || startTimestamp!! >= endTimestamp!!) {
                    Toast.makeText(this, "Invalid recurring dates selected.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (accountList.isEmpty()) {
                Toast.makeText(this, "Please add an account before submitting a transaction.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedAccountPosition = spinnerAccounts.selectedItemPosition
            if (selectedAccountPosition == Spinner.INVALID_POSITION) {
                Toast.makeText(this, "Please select an account.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val notes = editTextNotes.text.toString()
            val imageUrl = selectedImageUri?.toString() ?: ""

            val selectedAccount = accountList[selectedAccountPosition]
            val selectedAccountId = selectedAccount.accountId

            val transaction = TransactionData(
                transactionType = transactionType,
                amount = amount,
                category = selectedCategory,
                notes = if (notes.isNotBlank()) notes else null,
                recurring = isRecurring,
                frequency = selectedFrequency,
                startTimestamp = startTimestamp,
                endTimestamp = endTimestamp,
                imageUrl = imageUrl,
                accountId = selectedAccountId,
                timestamp = if (isRecurring) startTimestamp ?: System.currentTimeMillis() else System.currentTimeMillis()
            )

            firestoreRepo.addTransaction(
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener,
                transaction = transaction,
                onSuccess = {
                    Toast.makeText(this, "Transaction Captured!", Toast.LENGTH_SHORT).show()

                    editTextAmount.text.clear()
                    editTextNotes.text.clear()
                    selectedCategory = ""
                    textViewSelectCategory.text = "Select Category"
                    selectedImageUri = null
                    isRecurring = false
                    selectedFrequency = null
                    startTimestamp = null
                    endTimestamp = null
                    tvRecurring.setBackgroundResource(R.drawable.button_rounded_dark)

                    val intent = Intent(this, HomepageActivity::class.java)
                    startActivity(intent)
                    finish()
                }, onFailure = { e ->
                    Toast.makeText(this, "Failed to save transaction: ${e.message}", Toast.LENGTH_LONG).show()

                }
            )
        }
    }
}