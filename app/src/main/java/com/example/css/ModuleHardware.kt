package com.example.css

import android.content.ContentValues
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream

class ModuleHardware : BottomNav() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_module_hardware)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
            }
        }

        val backButton = findViewById<ImageButton>(R.id.back_button)
        val firstQuarterButton = findViewById<ImageButton>(R.id.download_firstqtr)
        val secondQuarterButton = findViewById<ImageButton>(R.id.download_secondqtr)
        val thirdQuarterButton = findViewById<ImageButton>(R.id.download_thirdqtr)
        val fourthQuarterButton = findViewById<ImageButton>(R.id.download_fourthqtr)

        backButton.setOnClickListener {
            finish() // Go back to the previous screen
        }

        firstQuarterButton.setOnClickListener {
            showDownloadConfirmationDialog("Hardware_Quarter_1.pdf")
        }

        secondQuarterButton.setOnClickListener {
            showDownloadConfirmationDialog("Hardware_Quarter_2.pdf")
        }

        thirdQuarterButton.setOnClickListener {
            showDownloadConfirmationDialog("Hardware_Quarter_3.pdf")
        }

        fourthQuarterButton.setOnClickListener {
            showDownloadConfirmationDialog("Hardware_Quarter_4.pdf")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Storage permission is required to download files", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDownloadConfirmationDialog(fileName: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Download PDF")
        builder.setMessage("Are you sure you want to download $fileName?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show()

            // Add delay before starting download
            android.os.Handler(mainLooper).postDelayed({
                copyPdfToDownloads(fileName)
            }, 1500) // 1500ms = 1.5 seconds

            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        // Set button text color after dialog is shown
        val darkGreen = resources.getColor(R.color.dark_green, theme) // Define this color in colors.xml

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(darkGreen)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(darkGreen)
    }

    private fun copyPdfToDownloads(fileName: String) {
        val inputStream = assets.open(fileName)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // For Android 10 and above using MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val resolver = contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                    Toast.makeText(this, "PDF saved to Download folder", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            // For Android 9 and below
            val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val outFile = File(downloadsPath, fileName)
            val outputStream = FileOutputStream(outFile)

            inputStream.copyTo(outputStream)
            Toast.makeText(this, "PDF saved to: ${outFile.absolutePath}", Toast.LENGTH_LONG).show()
        }

        inputStream.close()
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_module_hardware
}
