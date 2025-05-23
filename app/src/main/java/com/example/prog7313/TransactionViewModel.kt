package com.example.prog7313

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.room.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp

class TransactionViewModel(private val repository: TransactionRepo) : ViewModel() {

    //--------------------------------------------
    // Database functions
    //--------------------------------------------

    fun insertTransaction(transactionData: TransactionData) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertTransaction(transactionData)
        }
    }

    fun getTotalSpentByCategory() = liveData(Dispatchers.IO) {
        emit(repository.getTotalSpentByCategory())
    }

    fun getTransactionsForDate(selectedDateTimestamp: Long) = liveData(Dispatchers.IO) {
        emit(repository.getTransactionsForDate(selectedDateTimestamp))
    }

    fun getTransactionById(transactionId: Long) = liveData(Dispatchers.IO) {
        emit(repository.getTransactionById(transactionId))
    }

    fun deleteTransactionById(transactionId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteTransactionById(transactionId)
        }
    }
}

//--------------------------------------------
// Factory setup for view model
//--------------------------------------------

class TransactionViewModelFactory(private val repository: TransactionRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}