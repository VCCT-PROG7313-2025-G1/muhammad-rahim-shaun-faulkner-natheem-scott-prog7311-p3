package com.example.prog7313

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

class UploadPhoto : AppCompatActivity() {

    //--------------------------------------------
    // private variables
    //--------------------------------------------

    private lateinit var imageViewPreview: ImageView
    private lateinit var buttonSelectPhoto: Button
    private lateinit var buttonTakePhoto: Button
    private lateinit var buttonConfirmPhoto: Button

    private var selectedImageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1
    private val TAKE_PHOTO_REQUEST = 2

    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload_photo)

        //--------------------------------------------
        // UI binds
        //--------------------------------------------

        imageViewPreview = findViewById(R.id.imgPreview)
        buttonSelectPhoto = findViewById(R.id.btnSelectPhoto)
        buttonTakePhoto = findViewById(R.id.btnTakePhoto)
        buttonConfirmPhoto = findViewById(R.id.btnConfirmPhoto)

        //--------------------------------------------
        // Firebase blob storage reference
        //--------------------------------------------

        storageReference = FirebaseStorage.getInstance().reference.child("transaction_images")

        buttonSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        buttonTakePhoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }

        buttonConfirmPhoto.setOnClickListener {
            selectedImageUri?.let {
                buttonConfirmPhoto.isEnabled = false
                uploadImageToFirebase(it)
            } ?: run {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //--------------------------------------------
    // Function to send image string to transaction
    //--------------------------------------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    selectedImageUri = data?.data
                    imageViewPreview.setImageURI(selectedImageUri)
                }

                TAKE_PHOTO_REQUEST -> {
                    val photoBitmap = data?.extras?.get("data") as? Bitmap
                    photoBitmap?.let {
                        // Save bitmap temporarily to cache and get uri
                        val uri = saveBitmapToCache(it)
                        selectedImageUri = uri
                        imageViewPreview.setImageURI(selectedImageUri)
                    }
                }
            }
        }
    }

    //--------------------------------------------
    // Function to cache image
    //--------------------------------------------

    private fun saveBitmapToCache(bitmap: Bitmap): Uri {
        val file = File(cacheDir, "temp_photo.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
        return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
    }

    //--------------------------------------------
    // Image upload to firebase function
    //--------------------------------------------

    private fun uploadImageToFirebase(imageUri: Uri) {
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val imageRef = storageReference.child(fileName)

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val resultIntent = Intent()
                    resultIntent.putExtra("selectedImageUri", downloadUri.toString())
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image: ${it.message}", Toast.LENGTH_SHORT).show()
                buttonConfirmPhoto.isEnabled = true
            }
    }
}