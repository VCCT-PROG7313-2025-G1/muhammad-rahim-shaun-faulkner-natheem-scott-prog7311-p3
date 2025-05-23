package com.example.prog7313

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class TransactionViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TransactionViewModel
    private lateinit var repository: TransactionRepo
    private lateinit var mockObserver: Observer<List<CategoryTotal>>

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        repository = mock()
        viewModel = TransactionViewModel(repository)
        mockObserver = mock()

        viewModel.getTotalSpentByCategory().observeForever(mockObserver)
    }

    @Test
    fun `insertTransaction should call repository insertTransaction`() = runTest {
        val transactionData = TransactionData(
            id = 1,
            transactionType = "Expense",
            amount = 100.0,
            category = "Food",
            timestamp = System.currentTimeMillis(),
            recurring = false,
            frequency = "",
            startTimestamp = 0L,
            endTimestamp = 0L,
            notes = "Lunch at cafe",
            imageUrl = null
        )

        viewModel.insertTransaction(transactionData)

        verify(repository, times(1)).insertTransaction(transactionData)
    }

    @Test
    fun `getTotalSpentByCategory should return correct data`() = runTest {
        val expectedData = listOf(
            CategoryTotal(
                category = "Food",
                totalSpent = 100.0
            )
        )

        whenever(repository.getTotalSpentByCategory()).thenReturn(expectedData)

        val liveData = viewModel.getTotalSpentByCategory()

        liveData.observeForever(mockObserver)

        verify(repository).getTotalSpentByCategory()
        verify(mockObserver).onChanged(expectedData)
    }

    @Test
    fun `getTransactionsForDate should return transactions for specific date`() = runTest {
        val selectedDateTimestamp = 1633024800000L
        val expectedTransactions = listOf(
            TransactionData(
                id = 1,
                transactionType = "Expense",
                amount = 100.0,
                category = "Food",
                timestamp = selectedDateTimestamp,
                recurring = false,
                frequency = "",
                startTimestamp = 0L,
                endTimestamp = 0L,
                notes = "Lunch",
                imageUrl = null
            )
        )

        whenever(repository.getTransactionsForDate(selectedDateTimestamp)).thenReturn(expectedTransactions)

        val observer = mock<Observer<List<TransactionData>>>()
        viewModel.getTransactionsForDate(selectedDateTimestamp).observeForever(observer)

        verify(repository).getTransactionsForDate(selectedDateTimestamp)
        verify(observer).onChanged(expectedTransactions)
    }

    @Test
    fun `getTransactionById should return the correct transaction`() = runTest {
        val transactionId = 1L
        val expectedTransaction = TransactionData(
            id = 1,
            transactionType = "Expense",
            amount = 100.0,
            category = "Food",
            timestamp = System.currentTimeMillis(),
            recurring = false,
            frequency = "",
            startTimestamp = 0L,
            endTimestamp = 0L,
            notes = "Lunch at cafe",
            imageUrl = null
        )

        whenever(repository.getTransactionById(transactionId)).thenReturn(expectedTransaction)

        val observer = mock<Observer<TransactionData?>>()
        viewModel.getTransactionById(transactionId).observeForever(observer)

        verify(repository).getTransactionById(transactionId)
        verify(observer).onChanged(expectedTransaction)
    }

    @Test
    fun `deleteTransactionById should call repository deleteTransactionById`() = runTest {
        val transactionId = 1L

        viewModel.deleteTransactionById(transactionId)

        verify(repository, times(1)).deleteTransactionById(transactionId)
    }

    }