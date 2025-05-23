package com.example.prog7313
import androidx.room.Entity
import androidx.room.PrimaryKey

//--------------------------------------------
// User database table
//--------------------------------------------

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val username: String,
    val password: String
)