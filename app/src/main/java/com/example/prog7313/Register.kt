package com.example.prog7313

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    //--------------------------------------------
    // Private variables
    //--------------------------------------------

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //--------------------------------------------
        // UI component binds
        //--------------------------------------------

        val editTextFullName = findViewById<EditText>(R.id.txtFullName)
        val editTextEmail = findViewById<EditText>(R.id.txtEmail)
        val editTextUsername = findViewById<EditText>(R.id.txtUsername)
        val editTextPassword = findViewById<EditText>(R.id.txtregPassword)
        val editTextConfirmPassword =  findViewById<EditText>(R.id.txtConfirm)
        val buttonSubmit = findViewById<Button>(R.id.btnCreate)

        //--------------------------------------------
        // Click listener for submit button and logic
        //--------------------------------------------

        buttonSubmit.setOnClickListener {
            val fullName = editTextFullName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            if (fullName.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = FirebaseAuth.getInstance().currentUser?.uid
                                val userMap = hashMapOf(
                                    "fullName" to fullName,
                                    "userName" to username,
                                    "email" to email
                                )
                                FirebaseFirestore.getInstance().collection("users")
                                    .document(uid!!)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, Login::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
