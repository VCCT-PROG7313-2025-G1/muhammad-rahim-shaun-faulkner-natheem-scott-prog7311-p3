package com.example.prog7313


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date

class HomePageViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _totalIncome = MutableLiveData<Double>(0.0)
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpenses = MutableLiveData<Double>(0.0)
    val totalExpenses: LiveData<Double> = _totalExpenses

    private val _balanceLiveData = MutableLiveData<Double>(0.0)
    val balanceLiveData: LiveData<Double> = _balanceLiveData

    private val _progressPercent = MutableLiveData<Int>()
    val progressPercent: LiveData<Int> = _progressPercent

    private val _accountList = MutableLiveData<List<AccountData>>()
    val accountList: LiveData<List<AccountData>> = _accountList

    private var monthlyExpense = 0.0
    private var minGoal = 0.0
    private var maxGoal = 0.0

    fun calculateBalance() {
        db.collection("users")
            .document(userId)
            .collection("transactions")
            .get()
            .addOnSuccessListener { snapshot ->
                var income = 0.0
                var expenses = 0.0

                for (doc in snapshot) {
                    val amount = doc.getDouble("amount") ?: continue
                    when (doc.getString("transactionType")) {
                        "Income" -> income += amount
                        "Expense" -> expenses += amount
                    }
                }

                _totalIncome.value = income
                _totalExpenses.value = expenses
                _balanceLiveData.value = income - expenses
            }
    }

    fun loadMonthlyExpense() {
        db.collection("users")
            .document(userId)
            .collection("transactions")
            .get()
            .addOnSuccessListener { snapshot ->
                var totalExpense = 0.0

                val cal = Calendar.getInstance()
                val currentMonth = cal.get(Calendar.MONTH)
                val currentYear = cal.get(Calendar.YEAR)

                for (doc in snapshot) {
                    val amount = doc.getDouble("amount") ?: continue
                    val transactionType = doc.getString("transactionType")

                    val timestampNumber = doc.getLong("timestamp") ?: doc.getDouble("timestamp")?.toLong()

                    Log.d("HomePageViewModel", "Transaction doc ${doc.id} - type: $transactionType, amount: $amount, timestamp: $timestampNumber")

                    val timestampDate = if (timestampNumber != null) {
                        Date(timestampNumber)
                    } else {
                        null
                    }

                    if (timestampDate != null) {
                        cal.time = timestampDate
                        val txnMonth = cal.get(Calendar.MONTH)
                        val txnYear = cal.get(Calendar.YEAR)

                        Log.d("HomePageViewModel", "Transaction date month: $txnMonth, year: $txnYear")

                        if (transactionType == "Expense" && txnMonth == currentMonth && txnYear == currentYear) {
                            totalExpense += amount
                        }
                    } else {
                        Log.d("HomePageViewModel", "Transaction ${doc.id} has invalid timestamp")
                    }
                }

                Log.d("HomePageViewModel", "Total monthly expense calculated: $totalExpense")

                monthlyExpense = totalExpense
                updateProgress()
            }
    }

    fun refreshMonthlyExpense() {
        loadMonthlyExpense()
    }

    fun setMinGoal(value: Double) {
        minGoal = value
        updateProgress()
    }

    fun setMaxGoal(value: Double) {
        maxGoal = value
        updateProgress()
    }

    fun restoreGoals(min: Double, max: Double) {
        minGoal = min
        maxGoal = max
        updateProgress()
    }

    private fun updateProgress() {
        if (maxGoal == 0.0) {
            _progressPercent.value = 0
            return
        }

        val percent = ((monthlyExpense / maxGoal) * 100).coerceIn(0.0, 100.0)
        _progressPercent.value = percent.toInt()
    }

    fun loadAccounts() {
        db.collection("users")
            .document(userId)
            .collection("accounts")
            .get()
            .addOnSuccessListener { snapshot ->
                val accounts = snapshot.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("bankName")
                    val type = doc.getString("accountType")
                    val balance = doc.getDouble("balance")
                    if (name != null && type != null && balance != null) {
                        AccountData(id, name, type, balance)
                    } else null
                }
                _accountList.value = accounts
            }
    }
}