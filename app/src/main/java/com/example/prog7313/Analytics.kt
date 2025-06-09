package com.example.prog7313

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.graphics.Color
import android.widget.Button
import androidx.drawerlayout.widget.DrawerLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.exp

class Analytics : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_analytics)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Analytics"

        DrawerHelper.setupDrawer(this, drawerLayout, toolbar, navView)

        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)

        loadPieChartData()
        loadBarChartData()

        findViewById<Button>(R.id.btnExport).setOnClickListener {
            exportMonthlyTransactionsToCSV()
        }

    }

    private fun loadPieChartData() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .get()
            .addOnSuccessListener { snapshot ->
                val categoryTotals = mutableMapOf<String, Float>()

                for (doc in snapshot) {
                    val amount = doc.getDouble("amount")?.toFloat() ?: continue
                    val timestamp = doc.getLong("timestamp") ?: continue

                    val txCal = Calendar.getInstance().apply { timeInMillis = timestamp }
                    val txMonth = txCal.get(Calendar.MONTH)
                    val txYear = txCal.get(Calendar.YEAR)

                    if (txMonth == currentMonth && txYear == currentYear) {
                        val category = doc.getString("category") ?: "Other"
                        categoryTotals[category] = (categoryTotals[category] ?: 0f) + amount
                    }
                }

                if (categoryTotals.isEmpty()) {
                    Toast.makeText(this, "No data for this month", Toast.LENGTH_SHORT).show()
                }

                setPieChartData(categoryTotals)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setPieChartData(categoryTotals: Map<String, Float>) {
        val entries = categoryTotals.map { (category, total) ->
            PieEntry(total, category)
        }

        val softColors = listOf(
            0xFF8E9AAF.toInt(), // soft blue-gray
            0xFFB4A7D6.toInt(), // soft lavender
            0xFFB7C9E2.toInt(), // soft light blue
            0xFF9FBCB0.toInt(), // soft teal
            0xFFE6C9A8.toInt(), // soft beige
            0xFFE9AFA3.toInt(), // soft peach
            0xFFB2A4FF.toInt(), // soft violet
            0xFF97C1A9.toInt()  // soft mint green
        )

        val dataSet = PieDataSet(entries, "Category Breakdown").apply {
            colors = softColors
            valueTextColor = Color.WHITE
            valueTextSize = 10f
        }

        val pieData = PieData(dataSet)

        pieChart.apply {
            data = pieData
            description.isEnabled = false
            centerText = "Categories"
            setCenterTextSize(14f)
            setUsePercentValues(true)
            setEntryLabelColor(Color.WHITE)
            legend.textColor = Color.WHITE
            animateY(1000)
            invalidate()
        }
    }

    private fun loadBarChartData() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val currentMonthStr = sdf.format(calendar.time)

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .get()
            .addOnSuccessListener { snapshot ->
                var income = 0f
                var expense = 0f

                for (doc in snapshot) {
                    val amount = doc.getDouble("amount")?.toFloat() ?: continue
                    val timestamp = doc.getLong("timestamp") ?: continue

                    val txCal = Calendar.getInstance().apply { timeInMillis = timestamp }
                    val txMonth = txCal.get(Calendar.MONTH)
                    val txYear = txCal.get(Calendar.YEAR)

                    if (txMonth == currentMonth && txYear == currentYear) {
                        when (doc.getString("transactionType")) {
                            "Income" -> income += amount
                            "Expense" -> expense += amount
                        }
                    }
                }

                db.collection("users")
                    .document(userId)
                    .collection("goals")
                    .whereEqualTo("month", currentMonthStr)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { goalSnapshot ->

                        var minGoal = 0f
                        var maxGoal = 0f

                        if (!goalSnapshot.isEmpty) {
                            val goalDoc = goalSnapshot.documents[0]
                            minGoal = goalDoc.getDouble("minGoal")?.toFloat() ?: 0f
                            maxGoal = goalDoc.getDouble("maxGoal")?.toFloat() ?: 0f
                        }

                        val rawBalance = income - expense
                        val balance = rawBalance.coerceAtLeast(0f)
                        val clampedIncome = income.coerceAtLeast(0f)
                        val clampedExpense = expense.coerceAtLeast(0f)

                        if (rawBalance < 0f) {
                            Toast.makeText(this, "Warning: You have spent more than you earned this month!", Toast.LENGTH_LONG).show()
                        }

                        setBarChartData(balance, clampedExpense, clampedIncome, minGoal, maxGoal)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to load goals", Toast.LENGTH_SHORT).show()

                        val rawBalance = income - expense
                        val balance = rawBalance.coerceAtLeast(0f)
                        val clampedIncome = income.coerceAtLeast(0f)
                        val clampedExpense = expense.coerceAtLeast(0f)

                        if (rawBalance < 0f) {
                            Toast.makeText(this, "Warning: You have spent more than you earned this month!", Toast.LENGTH_LONG).show()
                        }
                        setBarChartData(balance, clampedExpense, clampedIncome, 0f, 0f)
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setBarChartData(income: Float, expense: Float, balance: Float, minGoal: Float, maxGoal: Float) {
        val entries = arrayListOf(
            BarEntry(0f, balance),
            BarEntry(1f, expense),
            BarEntry(2f, income),
            BarEntry(3f, minGoal),
            BarEntry(4f, maxGoal)
        )

        val dataSet = BarDataSet(entries, "Current Month Financials").apply {
            // Softer colors
            colors = listOf(
                0xFF81C784.toInt(), // Soft green for Balance
                0xFFE57373.toInt(), // Soft red for Expense
                0xFF64B5F6.toInt(), // Soft blue for Income
                0xFF4DB6AC.toInt(), // Soft teal for Min Goal
                0xFFFFB74D.toInt()  // Soft orange for Max Goal
            )
            valueTextColor = Color.WHITE
            valueTextSize = 14f
        }

        val data = BarData(dataSet)
        data.barWidth = 0.5f

        barChart.data = data

        val labels = listOf("Balance", "Expense", "Income", "Min Goal", "Max Goal")
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size
        xAxis.textColor = Color.WHITE

        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisRight.isEnabled = false

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false

        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun exportMonthlyTransactionsToCSV() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .get()
            .addOnSuccessListener { snapshot ->
                val filteredTransactions = snapshot.filter { doc ->
                    val timestamp = doc.getLong("timestamp") ?: return@filter false
                    val txCal = Calendar.getInstance().apply { timeInMillis = timestamp }
                    txCal.get(Calendar.MONTH) == currentMonth && txCal.get(Calendar.YEAR) == currentYear
                }

                if (filteredTransactions.isEmpty()) {
                    Toast.makeText(this, "No transactions to export", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val csvHeader = "Type,Amount,Category,Date,Notes\n"
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                val csvBody = filteredTransactions.joinToString("\n") { doc ->
                    val type = doc.getString("transactionType") ?: "Unknown"
                    val amount = doc.getDouble("amount")?.toString() ?: "0.00"
                    val category = doc.getString("category") ?: "Uncategorized"
                    val timestamp = doc.getLong("timestamp") ?: 0
                    val date = sdf.format(timestamp)
                    val notes = doc.getString("notes")?.replace(",", " ") ?: ""
                    "$type,$amount,$category,$date,$notes"
                }

                val fullCSV = csvHeader + csvBody

                try {
                    val fileName = "monthly_transactions_${System.currentTimeMillis()}.csv"
                    val file = File(getExternalFilesDir(null), fileName)
                    file.writeText(fullCSV)

                    Toast.makeText(this, "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load transactions", Toast.LENGTH_SHORT).show()
            }
    }
}