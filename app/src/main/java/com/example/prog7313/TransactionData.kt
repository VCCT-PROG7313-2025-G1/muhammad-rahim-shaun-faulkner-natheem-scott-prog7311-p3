package com.example.prog7313

import androidx.room.PrimaryKey
import androidx.room.Entity

//--------------------------------------------
// Transactions database table
//--------------------------------------------

@Entity(tableName = "transactions")
data class TransactionData (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transactionType: String,
    val recurring: Boolean,
    val frequency: String?,
    val startTimestamp: Long?,
    val endTimestamp: Long?,
    val amount: Double,
    val notes: String?,
    val category: String,
    val imageUrl: String?,
    val timestamp: Long
)