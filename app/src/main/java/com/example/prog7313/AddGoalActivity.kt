package com.example.prog7313

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddGoalActivity : AppCompatActivity() {

    private lateinit var etMinGoal: TextInputEditText
    private lateinit var etMaxGoal: TextInputEditText
    private lateinit var btnSaveGoal: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_goal)

        etMinGoal = findViewById(R.id.etMinGoal)
        etMaxGoal = findViewById(R.id.etMaxGoal)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)

        btnSaveGoal.setOnClickListener {
            saveGoalToFirestore()
        }
    }

    private fun saveGoalToFirestore() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val min = etMinGoal.text.toString().toDoubleOrNull()
        val max = etMaxGoal.text.toString().toDoubleOrNull()

        if (min == null || max == null || min > max) {
            Toast.makeText(this, "Enter valid min and max goal", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = Calendar.getInstance()
        val goalMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

        val goalData = mapOf(
            "minGoal" to min,
            "maxGoal" to max,
            "timestamp" to Timestamp.now(),
            "month" to goalMonth
        )

        db.collection("users")
            .document(userId)
            .collection("goals")
            .document(goalMonth)
            .set(goalData)
            .addOnSuccessListener {
                Toast.makeText(this, "Goal saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save goal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}