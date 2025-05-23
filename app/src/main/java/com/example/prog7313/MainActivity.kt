package com.example.prog7313

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //--------------------------------------------
        // references for buttons
        //--------------------------------------------

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)

        //--------------------------------------------
        // click listener for login button
        //--------------------------------------------

        btnLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)  // Use Login::class.java, NOT LoginActivity
            startActivity(intent)
        }

        //--------------------------------------------
        // click listener for create account
        //--------------------------------------------

        btnCreateAccount.setOnClickListener {
            val intent = Intent(this, Register::class.java)  // Use Register::class.java, NOT RegisterActivity
            startActivity(intent)
        }
    }
}
