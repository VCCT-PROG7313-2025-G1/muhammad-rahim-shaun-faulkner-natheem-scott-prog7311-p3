package com.example.prog7313

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.prog7313.R.anim.slide_in_right
import com.example.prog7313.R.anim.slide_out_left
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Timeline : AppCompatActivity() {

    //--------------------------------------------
    // Viewmodel and UI elements
    //--------------------------------------------

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateText: TextView
    private lateinit var selectedDateValue: TextView
    private lateinit var selectedDateDisplayLayout: LinearLayout
    private lateinit var transactionsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timeline)

        //--------------------------------------------
        // Bottom nav bar setup
        //--------------------------------------------

        setupNavigation()

        //--------------------------------------------
        // Initialized database, dao, repo, factory and viewmodel
        //--------------------------------------------

        val database = AppDatabase.getDatabase(applicationContext)
        val transactionDao = database.transactionDao()
        val repository = TransactionRepo(transactionDao)
        val factory = TransactionViewModelFactory(repository)
        transactionViewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]

        //--------------------------------------------
        // Binds for UI elements
        //--------------------------------------------

        calendarView = findViewById(R.id.calendarView)
        selectedDateText = findViewById(R.id.selectedDateText)
        selectedDateValue = findViewById(R.id.selectedDateValue)
        selectedDateDisplayLayout = findViewById(R.id.selectedDateDisplayLayout)
        transactionsTextView = findViewById(R.id.transactionsTextView)

        //--------------------------------------------
        // Set date picker functionality
        //--------------------------------------------

        calendarView.setOnDateChangeListener {_, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            selectedDateValue.text = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(selectedDate)

            transactionViewModel.getTransactionsForDate(selectedDate).observe(this, Observer { transactions ->
                displayTransactions(transactions)
            })
        }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        selectedDateValue.text = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(today)

        calendarView.date = today

        transactionViewModel.getTransactionsForDate(today).observe(this, Observer { transactions ->
            displayTransactions(transactions)
        })
    }

    //--------------------------------------------
    // Transactions diplsayed in list
    //--------------------------------------------

    private fun displayTransactions(transactions: List<TransactionData>) {
        val container = findViewById<LinearLayout>(R.id.dateContentLayout)
        container.removeAllViews()

        if (transactions.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = "No transactions for this date."
                textSize = 16f
                setTextColor(Color.GRAY)
            }
            container.addView(emptyView)
            return
        }

        val inflater = LayoutInflater.from(this)

        transactions.forEach { transaction ->
            val view = inflater.inflate(R.layout.item_user_transaction, container, false)

            val ivIcon = view.findViewById<ImageView>(R.id.ivCategoryIcon)
            val tvType = view.findViewById<TextView>(R.id.tvCustomName)
            val tvAmount = view.findViewById<TextView>(R.id.tvTransactionAmount)
            val tvDetails = view.findViewById<TextView>(R.id.tvViewDetails)

            ivIcon.setImageResource(
                if (transaction.transactionType == "Expense") R.drawable.ic_minus
                else R.drawable.ic_plus
            )

            tvType.text = transaction.transactionType
            tvAmount.text = "R ${transaction.amount}"
            tvAmount.setTextColor(if (transaction.transactionType == "Expense") Color.RED else Color.GREEN)

            tvDetails.setOnClickListener {
                val intent = Intent(this, IndividualTransactions::class.java)
                intent.putExtra("transactionId", transaction.id)
                startActivity(intent)
            }

            container.addView(view)
        }
    }

    //--------------------------------------------
    // Bottom nav setup
    //--------------------------------------------

    private fun setupNavigation() {

        val navHome = findViewById<LinearLayout>(R.id.navHome)
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

        navSettings.setOnClickListener {
            // Navigate to Settings Activity
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
            // https://www.geeksforgeeks.org/how-to-add-slide-animation-between-activities-in-android/
            overridePendingTransition(slide_in_right, slide_out_left)
        }
    }
}