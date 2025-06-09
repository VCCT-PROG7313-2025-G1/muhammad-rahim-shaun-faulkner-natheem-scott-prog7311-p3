package com.example.prog7313

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserCategoryViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<UserCategoryData>>()
    val categories: LiveData<List<UserCategoryData>> get() = _categories

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun loadAllCategories() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("categories")
            .get()
            .addOnSuccessListener { result ->
                val categoryList = result.documents.map { doc ->
                    UserCategoryData(
                        name = doc.getString("name") ?: "",
                        transactionType = doc.getString("transactionType") ?: "",
                        id = doc.id
                    )
                }
                _categories.value = categoryList
            }
            .addOnFailureListener { exception ->
                _categories.value = emptyList()
            }
    }

    fun loadCategories(transactionType: String?) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("categories")
            .whereEqualTo("transactionType", transactionType)
            .get()
            .addOnSuccessListener { result ->
                val categoryList = result.documents.map { doc ->
                    UserCategoryData(
                        name = doc.getString("name") ?: "",
                        transactionType = doc.getString("transactionType") ?: "",
                        id = doc.id
                    )
                }
                _categories.value = categoryList
            }
            .addOnFailureListener { exception ->
                _categories.value = emptyList()
            }
    }
}