package com.example.css

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class AdminUploadModule : AppCompatActivity() {

    private var pdfUri: Uri? = null
    private val firestore = FirebaseFirestore.getInstance()
    private var currentCategory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_upload_module)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val settingsButton = findViewById<ImageButton>(R.id.setting_button)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        val viewHardwareButton = findViewById<ImageButton>(R.id.btn_view_hardware)
        val viewSoftwareButton = findViewById<ImageButton>(R.id.btn_view_software)
        val uploadPDFSoftware = findViewById<ImageButton>(R.id.btn_upload_software)
        val uploadPDFHardware = findViewById<ImageButton>(R.id.btn_upload_hardware)

        settingsButton.setOnClickListener {
            val intent = Intent(this, TeacherAccountSettings::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, AdminHomePage::class.java)
            startActivity(intent)
        }

        viewHardwareButton.setOnClickListener {
            val intent = Intent(this, AdminViewUploadedModule::class.java)
            intent.putExtra("category", "hardware")
            startActivity(intent)
        }

        viewSoftwareButton.setOnClickListener {
            val intent = Intent(this, AdminViewUploadedModule::class.java)
            intent.putExtra("category", "software")
            startActivity(intent)
        }

        uploadPDFSoftware.setOnClickListener {
            currentCategory = "software"
            openPDFPicker()
        }

        uploadPDFHardware.setOnClickListener {
            currentCategory = "hardware"
            openPDFPicker()
        }
    }

    // ============================================
    // PDF PICKER LAUNCHER
    // ============================================
    private val pickPdfLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            pdfUri = uri
            uploadPDF()   // auto upload after picking
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    // ============================================
    // PICK PDF
    // ============================================
    private fun openPDFPicker() {
        pickPdfLauncher.launch("application/pdf")
    }

    // ============================================
    // UPLOAD PDF AS BASE64 TO FIRESTORE
    // ============================================
    private fun uploadPDF() {
        val uri = pdfUri ?: return

        val progress = ProgressDialog(this)
        progress.setMessage("Uploading...")
        progress.setCancelable(false)
        progress.show()
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: return

            val base64Pdf = Base64.encodeToString(bytes, Base64.DEFAULT)
            val fileName = getFileName(uri)

            val collectionPath = if (currentCategory == "software") {
                "attachments_software"
            } else {
                "attachments_hardware"
            }

            val docRef = firestore.collection(collectionPath).document(fileName)

            // 🔥 1. Check if the document already exists
            docRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    Toast.makeText(this, "File already exists! Choose another file.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                // 🔥 2. Upload if NOT existing
                val data = hashMapOf(
                    "file_name" to fileName,
                    "file_data" to base64Pdf,
                    "category" to currentCategory,
                    "created_date" to Timestamp.now()
                )

                docRef.set(data)
                    .addOnSuccessListener {
                        progress.dismiss()
                        Toast.makeText(
                            this,
                            "Uploaded successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "File too large! Maximum allowed size is 750 KB.", Toast.LENGTH_SHORT).show()
                        progress.dismiss()
                    }
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "file.pdf"

        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    name = it.getString(index)
                }
            }
        }
        return name
    }
}