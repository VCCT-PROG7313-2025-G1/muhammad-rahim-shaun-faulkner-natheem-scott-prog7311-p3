package com.example.prog7313

import androidx.room.Entity
import androidx.room.PrimaryKey

//--------------------------------------------
// Category database table
//--------------------------------------------

@Entity(tableName = "categories")
data class UserCategoryData (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val transactionType: String
)