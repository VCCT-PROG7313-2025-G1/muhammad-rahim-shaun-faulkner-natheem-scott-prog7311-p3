package com.example.prog7313
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


//--------------------------------------------
// Main database class
//--------------------------------------------

@Database(entities = [
    User::class,
    TransactionData::class,
    UserCategoryData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    //--------------------------------------------
    // Abstract methods
    //--------------------------------------------

    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun userCategoryDao(): UserCategoryDao

    //--------------------------------------------
    // Singleton pattern for one instance of database
    //--------------------------------------------

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}