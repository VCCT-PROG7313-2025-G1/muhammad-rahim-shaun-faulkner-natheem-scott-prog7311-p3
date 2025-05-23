package com.example.prog7313

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UploadPhoto : AppCompatActivity() {

    //--------------------------------------------
    // private variables
    //--------------------------------------------

    private lateinit var imageViewPreview: ImageView
    private lateinit var buttonSelectPhoto: Button
    private lateinit var buttonConfirmPhoto: Button
    private var selectedImageUri: Uri? = null

    //--------------------------------------------
    // Singleton for once off
    //--------------------------------------------

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload_photo)

        //--------------------------------------------
        // UI binds
        //--------------------------------------------

        imageViewPreview = findViewById(R.id.imgPreview)
        buttonSelectPhoto = findViewById(R.id.btnSelectPhoto)
        buttonConfirmPhoto = findViewById(R.id.btnConfirmPhoto)

        //--------------------------------------------
        // Click listener for select image
        //--------------------------------------------

        buttonSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        //--------------------------------------------
        // Click listener image confirm
        //--------------------------------------------

        buttonConfirmPhoto.setOnClickListener {
            selectedImageUri?.let {
                val resultIntent = Intent()
                resultIntent.putExtra("selectedImageUri", it.toString())
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    //--------------------------------------------
    // Function to send image string to transaction
    //--------------------------------------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (selectedImageUri != null) {
            val resultIntent = Intent()
            resultIntent.putExtra("selectedImageUri", selectedImageUri.toString())
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
}