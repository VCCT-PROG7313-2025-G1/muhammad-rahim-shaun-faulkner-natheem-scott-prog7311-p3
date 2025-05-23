package com.example.prog7313

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.Month

@Dao
interface TransactionDao {

    //--------------------------------------------
    // Database queries
    //--------------------------------------------

    @Insert
    suspend fun insertTransaction(transactionData: TransactionData)

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<TransactionData>

    @Query("SELECT category, SUM(amount) as totalSpent FROM transactions GROUP BY category")
    suspend fun getTotalSpentByCategory(): List<CategoryTotal>

    @Query("SELECT SUM(amount) FROM transactions WHERE transactionType = :type")
    suspend fun getTotalByType(type: String): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE transactionType = 'Expense' AND timestamp BETWEEN :startOfMonth AND :endOfMonth")
    suspend fun getMonthlyExpense(startOfMonth: Long, endOfMonth: Long): Double?

    @Query("SELECT * FROM transactions WHERE timestamp >= :startDate AND timestamp <= :endDate")
    suspend fun getTransactionsForDate(startDate: Long, endDate: Long): List<TransactionData>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Long): TransactionData?

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)
}