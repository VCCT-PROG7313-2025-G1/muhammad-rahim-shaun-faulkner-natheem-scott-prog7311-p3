package com.example.prog7313

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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
import java.text.SimpleDateFormat
import java.util.Locale


class HomepageActivity : AppCompatActivity() {

    //--------------------------------------------
    // View model declarations
    //--------------------------------------------

    private lateinit var progressBar: ProgressBar

    private lateinit var tvMinGoal: TextView
    private lateinit var tvMaxGoal: TextView

    private var minGoalValue = 0.0
    private var maxGoalValue = 0.0

    private lateinit var viewModel: HomePageViewModel

    //--------------------------------------------
    // Activity creation
    //--------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"

        DrawerHelper.setupDrawer(this, drawerLayout, toolbar, navView)

        tvMinGoal = findViewById(R.id.tvMinGoal)
        tvMaxGoal = findViewById(R.id.tvMaxGoal)

        val btnSetGoal = findViewById<Button>(R.id.btnSetGoal)

        btnSetGoal.setOnClickListener {
            val intent = Intent(this, AddGoalActivity::class.java)
            startActivity(intent)
        }

        val btnAddTransaction = findViewById<Button>(R.id.btnAddTransaction)

        btnAddTransaction.setOnClickListener {
            val intent = Intent(this, Transactions::class.java)
            startActivity(intent)
        }

        //--------------------------------------------
        // Initialized HomePageViewModel
        //--------------------------------------------

        viewModel = ViewModelProvider(this)[HomePageViewModel::class.java]

        //--------------------------------------------
        // Display and update balance
        //--------------------------------------------

        val totalBalanceText: TextView = findViewById(R.id.tvIncomeTotalDisplay)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_accounts)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        viewModel.calculateBalance()
        viewModel.loadAccounts()

        viewModel.accountList.observe(this) { accounts ->
            recyclerView.adapter = AccountAdapter(accounts)
            val totalBalance = accounts.sumOf { it.balance }
            totalBalanceText.text = "R %.2f".format(totalBalance)
        }

        //--------------------------------------------
        // Display income and expense
        //--------------------------------------------

        val tvIncomeAmount: TextView = findViewById(R.id.tvIncomeAmount)
        val tvExpenseAmount: TextView = findViewById(R.id.tvExpenseAmount)

        viewModel.totalIncome.observe(this) { income ->
            val formatted = "R %.2f".format(income)
            tvIncomeAmount.text = formatted
        }

        viewModel.totalExpenses.observe(this) { expenses ->
            tvExpenseAmount.text = "R %.2f".format(expenses)
        }

        //--------------------------------------------
        // Setup goal inputs and progress bar
        //--------------------------------------------

        progressBar = findViewById(R.id.progressBar)

        loadCurrentMonthGoals()

        //--------------------------------------------
        // Observe and update progress
        //--------------------------------------------

        viewModel.progressPercent.observe(this) { percent ->
            progressBar.progress = percent

            progressBar.post {
                updateProgressAndGoalLines(percent)
            }
        }

        //--------------------------------------------
        // Load and display spending per category
        //--------------------------------------------

        val categoryTotalsContainer = findViewById<LinearLayout>(R.id.categoryTotalsContainer)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .get()
            .addOnSuccessListener { snapshot ->
                val transactions = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TransactionData::class.java)
                }

                val categoryTotals = transactions
                    .groupBy { it.category }
                    .map { (category, txns) ->
                        CategoryTotal(category, txns.sumOf { it.amount })
                    }

                categoryTotalsContainer.removeAllViews()

                val categoryIcons = mapOf(
                    "housing" to R.drawable.ic_housing,
                    "healthcare" to R.drawable.ic_healthcare,
                    "takeout" to R.drawable.ic_takeout,
                    "groceries" to R.drawable.ic_groceries,
                    "transportation" to R.drawable.ic_transportation,
                    "utilities" to R.drawable.ic_ultilities,
                    "entertainment" to R.drawable.ic_entertainment,
                    "salary" to R.drawable.ic_salary,
                    "loan" to R.drawable.ic_loan,
                    "transfer" to R.drawable.ic_gift
                )

                categoryTotals.forEach { total ->
                    val view = layoutInflater.inflate(R.layout.item_category_total, categoryTotalsContainer, false)
                    val ivIcon = view.findViewById<ImageView>(R.id.ivCategoryIcon)
                    val tvName = view.findViewById<TextView>(R.id.tvCategoryName)
                    val tvTotal = view.findViewById<TextView>(R.id.tvCategoryTotal)

                    tvName.text = total.category
                    tvTotal.text = "R %.2f".format(total.totalSpent)
                    val iconResId = categoryIcons[total.category.lowercase()] ?: R.drawable.ic_universal
                    ivIcon.setImageResource(iconResId)

                    categoryTotalsContainer.addView(view)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load category totals", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadCurrentMonthGoals() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            .format(java.util.Date())

        db.collection("users")
            .document(userId)
            .collection("goals")
            .whereEqualTo("month", currentMonth)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("HomepageActivity", "Goals loaded, count: ${snapshot.size()}")
                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents[0]
                    minGoalValue = doc.getDouble("minGoal") ?: 0.0
                    maxGoalValue = doc.getDouble("maxGoal") ?: 0.0
                    Log.d("HomepageActivity", "minGoal=$minGoalValue maxGoal=$maxGoalValue")

                    tvMinGoal.text = "R %.2f".format(minGoalValue)
                    tvMaxGoal.text = "R %.2f".format(maxGoalValue)

                    viewModel.setMinGoal(minGoalValue)
                    viewModel.setMaxGoal(maxGoalValue)

                    val progressPercent = progressBar.progress
                    updateProgressAndGoalLines(progressPercent)

                } else {
                    minGoalValue = 0.0
                    maxGoalValue = 0.0
                    tvMinGoal.text = "R 0.00"
                    tvMaxGoal.text = "R 0.00"

                    viewModel.setMinGoal(0.0)
                    viewModel.setMaxGoal(0.0)

                    updateProgressAndGoalLines(0)
                    Log.d("HomepageActivity", "No goals found for current month")
                }

                viewModel.loadMonthlyExpense()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load monthly goals", Toast.LENGTH_SHORT).show()
            }
    }

    //--------------------------------------------
    // Update progress bar and goal indicators
    //--------------------------------------------

    private fun updateProgressAndGoalLines(currentPercent: Int = progressBar.progress) {
        val minGoalLine: View = findViewById(R.id.mindGoalLine)
        val maxGoalLine: View = findViewById(R.id.maxGoalLine)

        if (progressBar.width == 0 || maxGoalValue == 0.0) return

        val minGoalProgress = if (maxGoalValue > 0) (minGoalValue / maxGoalValue) * 100 else 0.0
        val maxGoalProgress = 100.0

        progressBar.progressTintList = when {
            currentPercent < minGoalProgress -> ColorStateList.valueOf(Color.BLUE)
            currentPercent <= maxGoalProgress -> ColorStateList.valueOf(Color.GREEN)
            else -> ColorStateList.valueOf(Color.RED)
        }

        val progressWidth = progressBar.width

        if ( minGoalValue> 0 && minGoalProgress > 0) {
            minGoalLine.visibility = View.VISIBLE
            minGoalLine.translationX = (progressWidth * (minGoalProgress / 100)).toFloat() - (minGoalLine.width / 2)
        } else {
            minGoalLine.visibility = View.INVISIBLE
        }

        if (maxGoalValue > 0) {
            maxGoalLine.visibility = View.VISIBLE
            maxGoalLine.translationX = (progressWidth * 1.0).toFloat() - (maxGoalLine.width / 2)
        } else {
            maxGoalLine.visibility = View.INVISIBLE
        }
    }
}