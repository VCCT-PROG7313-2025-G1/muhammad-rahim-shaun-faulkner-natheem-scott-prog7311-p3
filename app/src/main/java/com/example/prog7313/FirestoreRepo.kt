package com.example.prog7313

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirestoreRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun addTransaction(
        userId: String,
        transaction: TransactionData,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val transactionRef = firestore.collection("users")
            .document(userId)
            .collection("transactions")
            .document()

        if (transaction.accountId == null) {
            transactionRef.set(transaction)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
            return
        }

        val accountRef = firestore.collection("users")
            .document(userId)
            .collection("accounts")
            .document(transaction.accountId)

        firestore.runTransaction { transactionFirestore ->

            val accountSnapshot = transactionFirestore.get(accountRef)

            val currentBalance = accountSnapshot.getDouble("balance") ?: 0.0

            val newBalance = if (transaction.transactionType == "Expense") {
                currentBalance - transaction.amount
            } else {
                currentBalance + transaction.amount
            }

            transactionFirestore.set(transactionRef, transaction)

            transactionFirestore.update(accountRef, "balance", newBalance)

        } . addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            Log.e("FirestoreTransaction", "Transaction failed", e)
            onFailure(e)
        }
    }

    fun getTransactionsForAccount(
        userId: String,
        accountId: String,
        onComplete: (List<TransactionData>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("transactions")
            .whereEqualTo("accountId", accountId)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val transactions = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TransactionData::class.java)
                }
                onComplete(transactions)
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }
}