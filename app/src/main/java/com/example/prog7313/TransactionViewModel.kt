package com.example.prog7313

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class CategoryTotal(
    val category: String = "",
    val totalSpent: Double = 0.0
)

class TransactionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _categoryTotals = MutableLiveData<List<CategoryTotal>>()
    val categoryTotals: LiveData<List<CategoryTotal>> = _categoryTotals

    fun loadTotalSpentByCategory() {
        db.collection("users")
            .document(userId)
            .collection("transactions")
            .get()
            .addOnSuccessListener { snapshot ->
                val totalsMap = mutableMapOf<String, Double>()

                for (doc in snapshot) {
                    val transactionType = doc.getString("transactionType")
                    val category = doc.getString("category") ?: "Uncategorized"
                    val amount = doc.getDouble("amount") ?: continue

                    if (transactionType == "Expense") {
                        totalsMap[category] = (totalsMap[category] ?: 0.0) + amount
                    }
                }

                val result = totalsMap.map { CategoryTotal(it.key, it.value) }
                _categoryTotals.value = result
            }
    }
}