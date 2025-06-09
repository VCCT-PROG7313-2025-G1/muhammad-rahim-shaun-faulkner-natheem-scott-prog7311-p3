package com.example.prog7313

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class IndividualTransactions : AppCompatActivity() {

    //--------------------------------------------
    // Private variables
    //--------------------------------------------

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private lateinit var transactionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_individual_transactions)

        //--------------------------------------------
        // Transaction logic based on transaction ID
        //--------------------------------------------

        transactionId = intent.getStringExtra("transactionId") ?: ""
        if (transactionId.isEmpty() || currentUserId.isEmpty()) {
            Toast.makeText(this, "Transaction not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadTransaction()

        findViewById<Button>(R.id.btnDeleteTransaction).setOnClickListener {
            deleteTransaction()
        }
    }

    //--------------------------------------------
    // Populates UI fields
    //--------------------------------------------

    private fun loadTransaction() {
        db.collection("users")
            .document(currentUserId)
            .collection("transactions")
            .document(transactionId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val transaction = doc.toObject(TransactionData::class.java)
                    if (transaction != null) {
                        populateUI(transaction)
                    }
                } else {
                    Toast.makeText(this, "Transaction not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load transaction", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    //--------------------------------------------
    // Displays transaction image
    //--------------------------------------------

    private fun populateUI(data: TransactionData) {
        findViewById<TextView>(R.id.tvTransactionType).text = data.transactionType
        findViewById<TextView>(R.id.tvAmount).text = "R %.2f".format(data.amount)
        findViewById<TextView>(R.id.tvSelectedCategory).text = data.category
        findViewById<TextView>(R.id.tvNotes).text = data.notes ?: "No notes"
        findViewById<TextView>(R.id.tvRecurringStatus).text = if (data.recurring) "Yes" else "No"

        if (data.recurring) {
            findViewById<LinearLayout>(R.id.recurringOptionsContainer).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvFrequency).text = data.frequency ?: "N/A"
            findViewById<TextView>(R.id.tvStartDate).text = data.startTimestamp?.let { formatTimestamp(it) } ?: "N/A"
            findViewById<TextView>(R.id.tvEndDate).text = data.endTimestamp?.let { formatTimestamp(it) } ?: "No end date"
        } else {
            findViewById<LinearLayout>(R.id.recurringOptionsContainer).visibility = View.GONE
        }

        val imageStatus = if (data.imageUrl.isNullOrEmpty() || !File(data.imageUrl).exists()) {
            "No image attached"
        } else {
            "Image Available"
        }

        findViewById<TextView>(R.id.tvImageStatus).text = imageStatus

        val imgTransaction = findViewById<ImageView>(R.id.imgTransaction)

        if (!data.imageUrl.isNullOrEmpty()) {
            imgTransaction.visibility = View.VISIBLE

            Glide.with(this)
                .load(data.imageUrl)
                .into(imgTransaction)

            imgTransaction.setOnClickListener {
                showImagePopup(data.imageUrl!!)
            }

            findViewById<TextView>(R.id.tvImageStatus).text = "Image Available"

        } else {
            imgTransaction.visibility = View.GONE
            findViewById<TextView>(R.id.tvImageStatus).text = "No image attached"
        }

        // Load account name based on accountId
        val accountTextView = findViewById<TextView>(R.id.tvAccountId)

        if (!data.accountId.isNullOrEmpty()) {
            db.collection("users")
                .document(currentUserId)
                .collection("accounts")
                .document(data.accountId)
                .get()
                .addOnSuccessListener { doc ->
                    val accountName = doc.getString("bankName") ?: "Unknown Account"
                    val accountType = doc.getString("accountType") ?: "Unknown Type"
                    accountTextView.text = "${accountName} - ${accountType}"
                }
                .addOnFailureListener {
                    accountTextView.text = "Account: (error)"
                }
        } else {
            accountTextView.text = "Account: N/A"
        }
    }

    private fun deleteTransaction() {
        db.collection("users")
            .document(currentUserId)
            .collection("transactions")
            .document(transactionId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@IndividualTransactions, Timeline::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete transaction", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showImagePopup(imageUrl: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_image_popup)

        val imageView = dialog.findViewById<ImageView>(R.id.dialogImageView)

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}