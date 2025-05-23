package com.example.prog7313

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserCategoryDao {

    //--------------------------------------------
    // Database queries
    //--------------------------------------------

    @Insert
    suspend fun insert(category: UserCategoryData)

    @Query("SELECT * FROM categories WHERE transactionType = :transactionType")
    suspend fun getCategoriesByType(transactionType: String): List<UserCategoryData>

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<UserCategoryData>

    @Delete
    suspend fun delete(category: UserCategoryData)

}