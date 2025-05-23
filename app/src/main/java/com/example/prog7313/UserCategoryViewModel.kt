package com.example.prog7313

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserCategoryViewModel(application: Application) : AndroidViewModel(application) {

    //--------------------------------------------
    // private values
    //--------------------------------------------

    private val userCategoryDao: UserCategoryDao = AppDatabase.getDatabase(application).userCategoryDao()
    private val _categories = MutableLiveData<List<UserCategoryData>>()
    val categories: LiveData<List<UserCategoryData>> = _categories

    //--------------------------------------------
    // load category function
    //--------------------------------------------

    fun loadCategories(transactionType: String?) {
        if (transactionType != null) {
            viewModelScope.launch {
                val categoryList = userCategoryDao.getCategoriesByType(transactionType)
                _categories.postValue(categoryList)
            }
        }
    }

    fun loadAllCategories() {
        viewModelScope.launch {
            val allCategories = userCategoryDao.getAllCategories()
            _categories.postValue(allCategories)
        }
    }

    //--------------------------------------------
    // delete category function
    //--------------------------------------------

    fun deleteCategory(categoryId: UserCategoryData) {
        viewModelScope.launch {
            userCategoryDao.delete(categoryId)
            _categories.postValue(userCategoryDao.getCategoriesByType("Expense"))
        }
    }
}