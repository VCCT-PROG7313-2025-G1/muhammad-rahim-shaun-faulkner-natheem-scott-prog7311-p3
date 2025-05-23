package com.example.prog7313

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class UserCategoryViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: UserCategoryViewModel
    private lateinit var mockDao: UserCategoryDao
    private lateinit var mockObserver: Observer<List<UserCategoryData>>

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        mockDao = mock()
        viewModel = UserCategoryViewModel(mock())
        mockObserver = mock()

        viewModel.categories.observeForever(mockObserver)
    }

    @Test
    fun `loadCategories should update categories when data is loaded`() = runBlocking {
        val transactionType = "Expense"
        val expectedCategories = listOf(
            UserCategoryData(1, "Groceries", "Expense"),
            UserCategoryData(2, "Dining", "Expense")
        )

        whenever(mockDao.getCategoriesByType(transactionType)).thenReturn(expectedCategories)

        viewModel.loadCategories(transactionType)

        verify(mockDao).getCategoriesByType(transactionType)
        verify(mockObserver).onChanged(expectedCategories)
    }

    @Test
    fun `loadAllCategories should update categories when data is loaded`() = runBlockingTest {
        val expectedCategories = listOf(
            UserCategoryData(1, "Groceries", "Expense"),
            UserCategoryData(2, "Dining", "Expense")
        )

        whenever(mockDao.getAllCategories()).thenReturn(expectedCategories)

        viewModel.loadAllCategories()

        verify(mockDao).getAllCategories()
        verify(mockObserver).onChanged(expectedCategories)
    }

    @Test
    fun `deleteCategory should update categories after deletion`() = runBlockingTest {
        val categoryToDelete = UserCategoryData(1, "Groceries", "Expense")
        val updatedCategories = listOf(UserCategoryData(2, "Dining", "Expense"))

        whenever(mockDao.getCategoriesByType("Expense")).thenReturn(updatedCategories)

        viewModel.deleteCategory(categoryToDelete)

        verify(mockDao).delete(categoryToDelete)
        verify(mockDao).getCategoriesByType("Expense")
        verify(mockObserver).onChanged(updatedCategories)
    }
}