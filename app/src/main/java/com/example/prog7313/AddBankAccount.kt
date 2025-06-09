package com.example.prog7313

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddBankAccount : AppCompatActivity() {

    //--------------------------------------------
    // Private variables
    //--------------------------------------------

    private lateinit var bankNameEditText: EditText
    private lateinit var accountTypeAutoComplete: AutoCompleteTextView
    private lateinit var submitButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bank_account)

        //--------------------------------------------
        // Firebase initialization
        //--------------------------------------------

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //--------------------------------------------
        // UI binds
        //--------------------------------------------

        bankNameEditText = findViewById(R.id.etBankName)
        accountTypeAutoComplete = findViewById(R.id.actvAccountType)
        submitButton = findViewById(R.id.btnSubmitAccount)

        //--------------------------------------------
        // Sets list for account type selection
        //--------------------------------------------

        val accountTypes = listOf("Cheque", "Savings", "Credit Card")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, accountTypes)
        accountTypeAutoComplete.setAdapter(adapter)

        //--------------------------------------------
        // On submit listener to create a bank account for user
        //--------------------------------------------

        submitButton.setOnClickListener {
            val bankName = bankNameEditText.text.toString().trim()
            val accountType = accountTypeAutoComplete.text.toString().trim()

            var isValid = true

            if (bankName.isEmpty()) {
                bankNameEditText.error = "Please enter a bank name"
                isValid = false
            }

            if (accountType.isEmpty()) {
                accountTypeAutoComplete.error = "Please select an account type"
                isValid = false
            }

            if (isValid) {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newAccount = hashMapOf(
                    "bankName" to bankName,
                    "accountType" to accountType,
                    "balance" to 0.0
                )

                firestore.collection("users")
                    .document(userId)
                    .collection("accounts")
                    .add(newAccount)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Bank account added successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add bank account: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}