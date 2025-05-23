package com.example.prog7313

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.prog7313.R.anim.slide_in_right
import com.example.prog7313.R.anim.slide_out_left
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.widget.addTextChangedListener
import kotlin.math.roundToInt


class HomepageActivity : AppCompatActivity() {

    //--------------------------------------------
    // View model declarations
    //--------------------------------------------

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var tvCurrentDate: TextView
    private lateinit var etMinGoal: EditText
    private lateinit var etMaxGoal: EditText
    private lateinit var progressBar: ProgressBar
    private var totalBalanceValue = 0.0
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

        //--------------------------------------------
        // Initialized TransactionViewModel
        //--------------------------------------------

        val dao = AppDatabase.getDatabase(application).transactionDao()
        val repository = TransactionRepo(dao)
        val factory = TransactionViewModelFactory(repository)
        transactionViewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]

        //--------------------------------------------
        // Initialized HomePageViewModel
        //--------------------------------------------

        viewModel = ViewModelProvider(this)[HomePageViewModel::class.java]

        //--------------------------------------------
        // Bottom nav setup
        //--------------------------------------------

        setupNavigation()

        //--------------------------------------------
        // Display and update balance
        //--------------------------------------------

        val tvIncomeTotalDisplay: TextView = findViewById(R.id.tvIncomeTotalDisplay)

        viewModel.balanceLiveData.observe(this) { balance ->
            tvIncomeTotalDisplay.text = "R %.2f".format(balance)
            totalBalanceValue = balance
        }

        viewModel.calculateBalance()

        //--------------------------------------------
        // Display current date
        //--------------------------------------------

        tvCurrentDate = findViewById(R.id.tvDate)

        val date = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
        tvCurrentDate.text = date

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

        viewModel.loadMonthlyExpense()

        etMinGoal = findViewById(R.id.etMinGoal)
        etMaxGoal = findViewById(R.id.etMaxGoal)
        progressBar = findViewById(R.id.progressBar)

        val prefs = getSharedPreferences("budget_prefs", MODE_PRIVATE)
        val savedMin = prefs.getFloat("min_goal", 0f)
        val savedMax = prefs.getFloat("max_goal", 0f)

        etMinGoal.setText("R %.0f".format(savedMin))
        etMaxGoal.setText("R %.0f".format(savedMax))

        viewModel.setMinGoal(savedMin.toDouble())
        viewModel.setMaxGoal(savedMax.toDouble())

        //--------------------------------------------
        // Save and update min goal
        //--------------------------------------------

        etMinGoal.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus) {
                val input = etMinGoal.text.toString().replace("R", "").trim()
                minGoalValue = input.toDoubleOrNull() ?: 0.0
                viewModel.setMinGoal(minGoalValue)
                updateProgressAndGoalLines()
                saveGoals()
                etMinGoal.setText("R %.0f".format(minGoalValue))
            }
        }

        //--------------------------------------------
        // Save and update max goal
        //--------------------------------------------

        etMaxGoal.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus) {
                val input = etMaxGoal.text.toString().replace("R", "").trim()
                maxGoalValue = input.toDoubleOrNull() ?: 0.0
                viewModel.setMaxGoal(maxGoalValue)
                viewModel.refreshMonthlyExpense()
                updateProgressAndGoalLines()
                saveGoals()
                etMaxGoal.setText("R %.0f".format(maxGoalValue))
            }
        }

        //--------------------------------------------
        // Observe and update progress
        //--------------------------------------------

        viewModel.progressPercent.observe(this) { percent ->
            progressBar.progress = percent
            minGoalValue = etMinGoal.text.toString().replace("R", "").toDoubleOrNull() ?: 0.0
            maxGoalValue = etMaxGoal.text.toString().replace("R", "").toDoubleOrNull() ?: 0.0

            progressBar.post {
                updateProgressAndGoalLines(percent)
            }
        }

        //--------------------------------------------
        // Load and display spending per category
        //--------------------------------------------

        val categoryTotalsContainer = findViewById<LinearLayout>(R.id.categoryTotalsContainer)

        transactionViewModel.getTotalSpentByCategory().observe(this, Observer { categoryTotals ->
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
                tvTotal.text = "R ${total.totalSpent}"

                val iconResId = categoryIcons[total.category.lowercase()] ?: R.drawable.ic_universal
                ivIcon.setImageResource(iconResId)

                categoryTotalsContainer.addView(view)
            }
        })
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

    //--------------------------------------------
    // Save goal values
    //--------------------------------------------

    private fun saveGoals() {
        val prefs = getSharedPreferences("budget_prefs", MODE_PRIVATE)
        val editor = prefs.edit()

        val minInput = etMinGoal.text.toString().replace("R", "").trim()
        val maxInput = etMaxGoal.text.toString().replace("R", "").trim()

        editor.putFloat("min_goal", minInput.toFloatOrNull() ?: 0f)
        editor.putFloat("max_goal", maxInput.toFloatOrNull() ?: 0f)
        editor.apply()
    }

    //--------------------------------------------
    // Restore goal values
    //--------------------------------------------

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("budget_prefs", MODE_PRIVATE)
        val savedMin = prefs.getFloat("min_goal", 0f)
        val savedMax = prefs.getFloat("max_goal", 0f)

        etMinGoal.setText("R %.0f".format(savedMin))
        etMaxGoal.setText("R %.0f".format(savedMax))
        minGoalValue = savedMin.toDouble()
        maxGoalValue = savedMax.toDouble()

        viewModel.restoreGoals(minGoalValue, maxGoalValue)
        viewModel.calculateBalance()
    }

    //--------------------------------------------
    // Save goals before activity pause
    //--------------------------------------------

    override fun onPause() {
        super.onPause()
        saveGoals()
    }

    //--------------------------------------------
    // Bottom navigation setup
    //--------------------------------------------

    private fun setupNavigation() {

        val navTimeline = findViewById<LinearLayout>(R.id.navTimeline)
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)
        val buttonAddTransaction = findViewById<Button>(R.id.btnAddTransaction)

        //--------------------------------------------
        // Click listeners
        //--------------------------------------------

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

        buttonAddTransaction.setOnClickListener {
            val intent = Intent(this, Transactions::class.java)
            startActivity(intent)
        }
    }
}