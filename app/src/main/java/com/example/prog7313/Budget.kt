package com.example.prog7313

import androidx.room.Entity
import androidx.room.PrimaryKey

//--------------------------------------------
// Database table for budget
//--------------------------------------------

@Entity(tableName = "budget")
data class Budget (
    @PrimaryKey
    val id: Int = 1,
    val monthlyLimit: Double
)