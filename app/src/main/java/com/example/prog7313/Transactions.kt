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
import androidx.lifecycle.ViewModelProvider
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import com.example.prog7313.R.anim.slide_in_right
import com.example.prog7313.R.anim.slide_out_left
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Transactions : AppCompatActivity() {

    //--------------------------------------------
    // private variables
    //--------------------------------------------

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var textViewSelectCategory: TextView
    private var selectedCategory: String = ""

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

    //--------------------------------------------
    // Activity to open reccuring activity
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
    // funtion to start category selection activity
    //--------------------------------------------

    private val categoryActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            selectedCategory = data?.getStringExtra("selectedCategory") ?: ""

            selectedCategory?.let {
                textViewSelectCategory.text = it
            }
        }
    }

    //--------------------------------------------
    // funtion to start photo selection activity
    //--------------------------------------------

    private  val photoActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            @Suppress("DEPRECATION")
            selectedImageUri = data?.getParcelableExtra<Uri>("selectedImageUri")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transactions)

        //--------------------------------------------
        // bottom nav bar setup
        //--------------------------------------------

        setupNavigation()

        //--------------------------------------------
        // Initialized database, dao, repo and viewmodel
        //--------------------------------------------

        val database = AppDatabase.getDatabase(this)
        val transactionDao = database.transactionDao()
        val repository = TransactionRepo(transactionDao)
        transactionViewModel = ViewModelProvider(this, TransactionViewModelFactory(repository)).get(TransactionViewModel::class.java)

        //--------------------------------------------
        // UI binds
        //--------------------------------------------

        val radioGroupTransactionType = findViewById<RadioGroup>(R.id.transactionTypeGroup)
        val radioIncome = findViewById<RadioButton>(R.id.rbIncome)
        val radioExpense = findViewById<RadioButton>(R.id.rbExpense)
        val editTextAmount = findViewById<EditText>(R.id.etAmount)
        val editTextNotes = findViewById<EditText>(R.id.etNotes)
        val buttonSubmit = findViewById<Button>(R.id.btnSubmit)

        //--------------------------------------------
        // Function to select category
        //--------------------------------------------

        textViewSelectCategory = findViewById(R.id.tvSelectCategory)

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

        uploadPhotoButton = findViewById(R.id.tvUploadPhoto)

        uploadPhotoButton.setOnClickListener {
            val intent = Intent(this, UploadPhoto::class.java)
            photoActivityLauncher.launch(intent)
        }

        //--------------------------------------------
        // recurring activity click listener
        //--------------------------------------------

        val tvRecurring = findViewById<TextView>(R.id.tvRecurring)
        var isRecurring = false

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

            if (transactionType.isNotEmpty()) {
                val amount = editTextAmount.text.toString().toDoubleOrNull()
                val notes = editTextNotes.text.toString()

                if (isRecurring && (startTimestamp == null || endTimestamp == null || startTimestamp!! >= endTimestamp!!)) {
                    Toast.makeText(this, "Invalid recurring dates selected.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (amount != null) {
                    val transactionData = TransactionData(
                        transactionType = transactionType,
                        recurring = isRecurring,
                        frequency = selectedFrequency,
                        startTimestamp = startTimestamp,
                        endTimestamp = endTimestamp,
                        timestamp = if (isRecurring) startTimestamp ?: System.currentTimeMillis() else System.currentTimeMillis(),
                        amount = amount,
                        notes = notes,
                        category = selectedCategory,
                        imageUrl = selectedImageUri.toString() ?: ""
                    )

                    transactionViewModel.insertTransaction(transactionData)

                    Toast.makeText(this, "Transaction Captured!", Toast.LENGTH_SHORT).show()

                    editTextAmount.text.clear()
                    editTextNotes.text.clear()

                    val intent = Intent(this, HomepageActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, "Please enter a valid amount!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select a transaction type!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //--------------------------------------------
    // bottom nav bar setup
    //--------------------------------------------

    private fun setupNavigation() {

        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTimeline = findViewById<LinearLayout>(R.id.navTimeline)
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)

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