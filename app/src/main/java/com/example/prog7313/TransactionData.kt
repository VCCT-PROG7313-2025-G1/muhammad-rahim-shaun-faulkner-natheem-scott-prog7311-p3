package com.example.prog7313

import com.google.firebase.firestore.Exclude

data class TransactionData(
    @get:Exclude @set:Exclude
    var id: String = "",

    val transactionType: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val notes: String? = null,
    val recurring: Boolean = false,
    val frequency: String? = null,
    val startTimestamp: Long? = null,
    val endTimestamp: Long? = null,
    val imageUrl: String? = null,
    val accountId: String? = null,
    val timestamp: Long? = null
)