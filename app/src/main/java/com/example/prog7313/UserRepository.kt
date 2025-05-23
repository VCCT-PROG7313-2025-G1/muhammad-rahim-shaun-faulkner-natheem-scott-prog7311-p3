package com.example.prog7313

class UserRepository(private val userDao: UserDao) {

    //--------------------------------------------
    // Functions for database
    //--------------------------------------------

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }
}