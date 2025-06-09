package com.example.prog7313

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditAccountActivity : AppCompatActivity() {

    private lateinit var bankNameEditText: EditText
    private lateinit var accountTypeAutoComplete: AutoCompleteTextView
    private lateinit var balanceEditText: EditText
    private lateinit var updateButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var accountId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_bank_account)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        bankNameEditText = findViewById(R.id.etBankName)
        accountTypeAutoComplete = findViewById(R.id.actvAccountType)
        balanceEditText = findViewById(R.id.etBalance)
        updateButton = findViewById(R.id.btnUpdateAccount)

        val accountTypes = listOf("Cheque", "Savings", "Credit Card")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, accountTypes)
        accountTypeAutoComplete.setAdapter(adapter)

        accountId = intent.getStringExtra("accountId")
        val bankName = intent.getStringExtra("bankName") ?: ""
        val accountType = intent.getStringExtra("accountType") ?: ""
        val balance = intent.getDoubleExtra("balance", 0.0)

        bankNameEditText.setText(bankName)
        accountTypeAutoComplete.setText(accountType, false)
        balanceEditText.setText(balance.toString())

        updateButton.setOnClickListener {
            val updatedBankName = bankNameEditText.text.toString().trim()
            val updatedAccountType = accountTypeAutoComplete.text.toString().trim()
            val balanceText = balanceEditText.text.toString().trim()

            var isValid = true

            if (updatedBankName.isEmpty()) {
                bankNameEditText.error = "Please enter a bank name"
                isValid = false
            }

            if (updatedAccountType.isEmpty()) {
                accountTypeAutoComplete.error = "Please select an account type"
                isValid = false
            }

            val updatedBalance = balanceText.toDoubleOrNull()
            if (updatedBalance == null) {
                balanceEditText.error = "Please enter a valid balance"
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            val userId = auth.currentUser?.uid
            if (userId == null) {
                Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update the Firestore document for this account
            if (accountId == null) {
                Toast.makeText(this, "Invalid account ID!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedAccount = hashMapOf(
                "bankName" to updatedBankName,
                "accountType" to updatedAccountType,
                "balance" to updatedBalance!!
            )

            firestore.collection("users")
                .document(userId)
                .collection("accounts")
                .document(accountId!!)
                .set(updatedAccount)
                .addOnSuccessListener {
                    Toast.makeText(this, "Account updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to update account: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}