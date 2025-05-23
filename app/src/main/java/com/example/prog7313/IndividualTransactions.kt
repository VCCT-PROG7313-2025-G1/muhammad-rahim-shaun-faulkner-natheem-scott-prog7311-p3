package com.example.prog7313

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Path
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.prog7313.R.anim.slide_in_right
import com.example.prog7313.R.anim.slide_out_left
import org.w3c.dom.Text
import java.io.File

class IndividualTransactions : AppCompatActivity() {

    //--------------------------------------------
    // Private variables
    //--------------------------------------------

    private lateinit var viewModel: TransactionViewModel
    private var transactionId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_individual_transactions)

        //--------------------------------------------
        // Bottom nav setup
        //--------------------------------------------

        setupNavigation()

        //--------------------------------------------
        // Transaction logic based on transaction ID
        //--------------------------------------------

        transactionId = intent.getLongExtra("transactionId", -1)

        val repo = TransactionRepo(AppDatabase.getDatabase(this).transactionDao())
        viewModel = ViewModelProvider(this, TransactionViewModelFactory(repo)).get(TransactionViewModel::class.java)

        viewModel.getTransactionById(transactionId).observe(this) { transaction ->
            transaction?.let { populateUI(it) }
        }

        findViewById<Button>(R.id.btnDeleteTransaction).setOnClickListener {
            viewModel.deleteTransactionById(transactionId)
            Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@IndividualTransactions, Timeline::class.java)
            startActivity(intent)
            finish()
        }
    }

    //--------------------------------------------
    // Populates UI fields
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
        }

        val imageStatus = if (data.imageUrl.isNullOrEmpty() || !File(data.imageUrl).exists()) {
            "No image attached"
        } else {
            "Image Available"
        }

        findViewById<TextView>(R.id.tvImageStatus).text = imageStatus

        val imgTransaction = findViewById<ImageView>(R.id.imgTransaction)

        if (!data.imageUrl.isNullOrEmpty() && File(data.imageUrl).exists()) {
            imgTransaction.visibility = View.VISIBLE
            val bitmap = BitmapFactory.decodeFile(data.imageUrl)
            imgTransaction.setImageBitmap(bitmap)

            imgTransaction.setOnClickListener {
                showImagePopup(data.imageUrl!!)
            }
        } else {
            imgTransaction.visibility = View.GONE
        }
    }

    //--------------------------------------------
    // Displays transaction image
    //--------------------------------------------

    private fun showImagePopup(imagePath: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_image_popup)

        val imageView = dialog.findViewById<ImageView>(R.id.dialogImageView)

        val bitmap = BitmapFactory.decodeFile(imagePath)
        imageView.setImageBitmap(bitmap)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    //--------------------------------------------
    // Timestamp conversion
    //--------------------------------------------

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    private fun setupNavigation() {

        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTimeline = findViewById<LinearLayout>(R.id.navTimeline)
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)

        //--------------------------------------------
        // Click listeners
        //--------------------------------------------


        navHome.setOnClickListener {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
            // https://www.geeksforgeeks.org/how-to-add-slide-animation-between-activities-in-android/
            overridePendingTransition(slide_in_right, slide_out_left)
        }

        navTimeline.setOnClickListener {
            // Navigate to Timeline Activity
            val intent = Intent(this, Timeline::class.java)
            startActivity(intent)
            // https://www.geeksforgeeks.org/how-to-add-slide-animation-between-activities-in-android/
            overridePendingTransition(slide_in_right, slide_out_left)
        }

        navSettings.setOnClickListener {
            // Navigate to Settings Activity
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
            // https://www.geeksforgeeks.org/how-to-add-slide-animation-between-activities-in-android/
            overridePendingTransition(slide_in_right, slide_out_left)
        }
    }
}