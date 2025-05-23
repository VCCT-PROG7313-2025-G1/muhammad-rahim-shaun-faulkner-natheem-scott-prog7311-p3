package com.example.prog7313

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.*

class UserViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: UserViewModel
    private lateinit var repository: UserRepository

    @Before
    fun setUp() {

        repository = mock()

        viewModel = UserViewModel(repository)
    }

    @Test
    fun `insertUser should call repository insertUser`() = runBlocking {

        val user = User(fullName = "John Doe", username = "jdoe", password = "password")

        viewModel.insertUser(user)

        verify(repository, times(1)).insertUser(user)
    }

    @Test
    fun `getUserByUsername should return the correct user`() = runBlocking {

        val user = User(fullName = "John Doe", username = "jdoe", password = "password")

        whenever(repository.getUserByUsername("jdoe")).thenReturn(user)

        val observer = mock<Observer<User?>>()
        viewModel.getUserByUsername("jdoe").observeForever(observer)

        verify(observer).onChanged(user)
    }
}