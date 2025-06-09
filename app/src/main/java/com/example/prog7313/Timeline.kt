package com.example.prog7313

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Timeline : AppCompatActivity() {

    //--------------------------------------------
    // Viewmodel and UI elements
    //--------------------------------------------

    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateValue: TextView
    private lateinit var transactionsTextView: TextView

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timeline)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Timeline"

        DrawerHelper.setupDrawer(this, drawerLayout, toolbar, navView)

        //--------------------------------------------
        // Binds for UI elements
        //--------------------------------------------

        calendarView = findViewById(R.id.calendarView)
        selectedDateValue = findViewById(R.id.selectedDateValue)
        transactionsTextView = findViewById(R.id.transactionsTextView)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            selectedDateValue.text = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(selectedDate)
            loadTransactionsForDate(selectedDate)
        }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        selectedDateValue.text = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(today)
        calendarView.date = today

        loadTransactionsForDate(today)
    }

    private fun loadTransactionsForDate(dateMillis: Long) {
        if (currentUserId.isEmpty()) return

        // Load only transactions on that specific day
        val startOfDay = dateMillis
        val endOfDay = dateMillis + 24 * 60 * 60 * 1000

        db.collection("users")
            .document(currentUserId)
            .collection("transactions")
            .whereGreaterThanOrEqualTo("timestamp", startOfDay)
            .whereLessThan("timestamp", endOfDay)
            .get()
            .addOnSuccessListener { snapshot ->
                val transactions = snapshot.documents.mapNotNull { doc ->
                    val transaction = doc.toObject(TransactionData::class.java)
                    transaction?.copy(id = doc.id)
                }
                displayTransactions(transactions)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load transactions", Toast.LENGTH_SHORT).show()
            }
    }

    //--------------------------------------------
    // Transactions displayed in list
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
}