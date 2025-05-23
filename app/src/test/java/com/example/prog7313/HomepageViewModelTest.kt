package com.example.prog7313

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.mockito.kotlin.*
import android.app.Application

@ExperimentalCoroutinesApi
class HomepageViewModelTest {

    private lateinit var viewModel: HomePageViewModel
    private lateinit var mockRepo: TransactionRepo
    private lateinit var mockObserver: Observer<Double>

    @Before
    fun setUp() {
        mockRepo = mock()
        val mockApplication: Application = mock()

        viewModel = HomePageViewModel(mockApplication)
        viewModel::class.java.getDeclaredField("repo").apply {
            isAccessible = true
            set(viewModel, mockRepo)
        }
    }

    @Test
    fun `calculateBalance should update balance, totalIncome, totalExpenses`() = runBlockingTest {
        val mockIncome = 500.0
        val mockExpenses = 300.0

        whenever(mockRepo.getTotalIncome()).thenReturn(mockIncome)
        whenever(mockRepo.getTotalExpenses()).thenReturn(mockExpenses)

        viewModel.balanceLiveData.observeForever(mockObserver)

        viewModel.calculateBalance()

        verify(mockRepo).getTotalIncome()
        verify(mockRepo).getTotalExpenses()
        verify(mockObserver).onChanged(mockIncome - mockExpenses)

        assert(viewModel.totalIncome.value == mockIncome)
        assert(viewModel.totalExpenses.value == mockExpenses)
    }

    @Test
    fun `loadMonthlyExpense should update monthlyExpenseLiveData`() = runBlockingTest {
        val mockExpense = 200.0

        whenever(mockRepo.getMonthlyExpense()).thenReturn(mockExpense)

        viewModel.monthlyExpenseLiveData.observeForever(mockObserver)

        viewModel.loadMonthlyExpense()

        verify(mockRepo).getMonthlyExpense()
        verify(mockObserver).onChanged(mockExpense)

        assert(viewModel.monthlyExpenseLiveData.value == mockExpense)
    }

    @Test
    fun `setMinGoal should update the minGoal LiveData and progress bar`() {
        val minGoal = 500.0

        viewModel.setMinGoal(minGoal)

        assert(viewModel.minGoal.value == minGoal)
    }

    @Test
    fun `setMaxGoal should update the maxGoal LiveData and progress bar`() {
        val maxGoal = 1000.0

        viewModel.setMaxGoal(maxGoal)

        assert(viewModel.maxGoal.value == maxGoal)
    }

    @Test
    fun `updateProgressBar should calculate progress based on expenses and goals`() = runBlocking {

        viewModel.restoreGoals(100.0, 200.0)

        val mockExpenses = 50.0
        val mockIncome = 100.0
        whenever(mockRepo.getTotalIncome()).thenReturn(mockIncome)
        whenever(mockRepo.getTotalExpenses()).thenReturn(mockExpenses)

        val observer = mock<Observer<Int>>()
        viewModel.progressPercent.observeForever(observer)

        viewModel.calculateBalance()

        val expectedProgress = ((mockExpenses / 200.0) * 100).toInt()

        verify(observer).onChanged(expectedProgress)
    }

    @Test
    fun `restoreGoals should update both minGoal, maxGoal, and progress bar`() = runBlocking {

        val mockRepo = mock<TransactionRepo>()

        viewModel = HomePageViewModel(Application())

        viewModel::class.java.getDeclaredField("repo").apply {
            isAccessible = true
            set(viewModel, mockRepo)
        }

        val progressObserver = mock<Observer<Int>>()
        viewModel.progressPercent.observeForever(progressObserver)

        val minGoal = 100.0
        val maxGoal = 200.0
        viewModel.restoreGoals(minGoal, maxGoal)

        assert(viewModel.minGoal.value == minGoal)
        assert(viewModel.maxGoal.value == maxGoal)

        val mockExpenses = 50.0
        whenever(mockRepo.getTotalExpenses()).thenReturn(mockExpenses)

        val expectedProgress = ((mockExpenses / maxGoal) * 100).toInt()

        verify(progressObserver).onChanged(expectedProgress)
    }
}