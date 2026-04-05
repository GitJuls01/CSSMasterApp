package com.example.css

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminViewUploadedModule : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_view_uploaded_module)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()

        // Back Button
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            startActivity(Intent(this, AdminUploadModule::class.java))
        }

        // Title text
        val headText = findViewById<TextView>(R.id.head_text)
        val category = intent.getStringExtra("category") ?: ""

        headText.text = when (category) {
            "hardware" -> "Hardware"
            "software" -> "Software"
            else -> "Modules"
        }

        fetchModule()
    }

    private fun fetchModule() {
        val moduleContainer = findViewById<LinearLayout>(R.id.module_container)
        moduleContainer.removeAllViews()

        val category = intent.getStringExtra("category") ?: ""

        // Choose correct Firestore table
        val table = when (category) {
            "hardware" -> "attachments_hardware"
            "software" -> "attachments_software"
            else -> "attachments_hardware" // default
        }

        // Fetch Firestore data
        firestore.collection(table)
            .orderBy("created_date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->

                if (result.isEmpty) {
                    val emptyText = TextView(this)
                    emptyText.text = "No uploaded modules found."
                    emptyText.textSize = 16f
                    emptyText.setPadding(20, 20, 20, 20)
                    moduleContainer.addView(emptyText)
                    return@addOnSuccessListener
                }

                for (document in result) {
                    val moduleView = layoutInflater.inflate(
                        R.layout.admin_view_module_item,
                        moduleContainer,
                        false
                    )

                    val nameText = moduleView.findViewById<TextView>(R.id.name_text)
                    val deleteButton = moduleView.findViewById<ImageView>(R.id.delete_button)

                    val fileName = document.getString("file_name") ?: "No Name"
                    nameText.text = fileName

                    deleteButton.setOnClickListener {
                        val dialog = AlertDialog.Builder(this)
                            .setTitle("Confirm Deletion")
                            .setMessage("Are you sure you want to delete this PDF?")
                            .setCancelable(false)
                            .setPositiveButton("Yes") { _, _ ->
                                firestore.collection(table)
                                    .document(document.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()

                                        fetchModule() // REFRESH UI after delete
                                    }
                            }
                            .setNegativeButton("No") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .create()

                        dialog.setOnShowListener {
                            val darkGreen = getColor(R.color.dark_green)
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(darkGreen)
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(darkGreen)
                        }

                        dialog.show()
                    }

                    moduleContainer.addView(moduleView)
                }
            }
            .addOnFailureListener { e ->
                Log.e("AdminModules", "Error loading modules", e)
            }
    }
}