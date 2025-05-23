package com.example.prog7313

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class Register : AppCompatActivity() {

    //--------------------------------------------
    // Private variables
    //--------------------------------------------

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //--------------------------------------------
        // Initialized database, dao, repo and viewmodel
        //--------------------------------------------

        val database = AppDatabase.getDatabase(this)
        val userDao = database.userDao()
        val repository = UserRepository(userDao)
        userViewModel = ViewModelProvider(this, UserViewModelFactory(repository)).get(UserViewModel::class.java)

        //--------------------------------------------
        // UI component binds
        //--------------------------------------------

        val editTextFullName = findViewById<EditText>(R.id.txtFullName)
        val editTextUsername = findViewById<EditText>(R.id.txtUsername)
        val editTextPassword = findViewById<EditText>(R.id.txtregPassword)
        val editTextConfirmPassword =  findViewById<EditText>(R.id.txtConfirm)
        val buttonSubmit = findViewById<Button>(R.id.btnCreate)

        //--------------------------------------------
        // Click listener for submit button and logic
        //--------------------------------------------

        buttonSubmit.setOnClickListener {
            val fullName = editTextFullName.text.toString().trim()
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            if (fullName.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    val newUser =
                        User(fullName = fullName, username = username, password = password)
                    userViewModel.insertUser(newUser)

                    Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
