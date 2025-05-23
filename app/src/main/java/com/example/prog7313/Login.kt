package com.example.prog7313

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer

class Login : AppCompatActivity() {

    //--------------------------------------------
    // View model for user database
    //--------------------------------------------

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //--------------------------------------------
        // Initialized database, dao, repo and viewmodel
        //--------------------------------------------

        val database = AppDatabase.getDatabase(this)
        val userDao = database.userDao()
        val repository = UserRepository(userDao)
        userViewModel = ViewModelProvider(this, UserViewModelFactory(repository)).get(UserViewModel::class.java)

        //--------------------------------------------
        // Component references
        //--------------------------------------------

        val editTextUsername = findViewById<EditText>(R.id.txtUsername)
        val editTextPassword = findViewById<EditText>(R.id.txtPassword)
        val buttonLogin = findViewById<Button>(R.id.btnLog)

        //--------------------------------------------
        // Login button click logic
        //--------------------------------------------

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                userViewModel.getUserByUsername(username).observe(this, Observer { user ->
                    if (user != null) {
                        if (user.password == password) {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, HomepageActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Incorrect Password!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
