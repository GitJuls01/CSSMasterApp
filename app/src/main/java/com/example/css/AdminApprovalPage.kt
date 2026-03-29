package com.example.css

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class AdminApprovalPage : AppCompatActivity() {

    private var backPressedOnce = false
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_approval_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Register the back press callback to handle back button presses
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    // Exit the app
                    finishAffinity() // Properly exits the app from this point
                } else {
                    backPressedOnce = true
                    // Show a Toast message
                    Toast.makeText(applicationContext, "Double click to exit the app", Toast.LENGTH_SHORT).show()

                    // Reset backPressedOnce flag after 2 seconds
                    android.os.Handler().postDelayed({
                        backPressedOnce = false
                    }, 2000) // Reset after 2 seconds
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        firestore = FirebaseFirestore.getInstance()
        val settingsButton = findViewById<ImageButton>(R.id.setting_button)
        val backButton = findViewById<ImageButton>(R.id.back_button)

        settingsButton.setOnClickListener {
            val intent = Intent(this, TeacherAccountSettings::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, AdminHomePage::class.java)
            startActivity(intent)
        }

        fetchUserTeacherCreatedBy()

    }

    private fun fetchUserTeacherCreatedBy() {
        val emailContainer = findViewById<LinearLayout>(R.id.email_container)
        emailContainer.removeAllViews()

        firestore.collection("users")
            .whereEqualTo("role", "teacher") // ✅ FIXED
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val userId = document.id
                    val email = document.getString("email") ?: "No Email"
                    val name = document.getString("name") ?: "No Name"
                    val section = document.getString("section") ?: "No Section"
                    val isApproved = document.getString("isApproved") ?: ""

                    val teacherView = layoutInflater.inflate(R.layout.admin_item_view, emailContainer, false)

                    val emailText = teacherView.findViewById<TextView>(R.id.email_text)
                    val viewDetailsButton = teacherView.findViewById<ImageView>(R.id.admin_view_details)
                    val adminApprovedButton = teacherView.findViewById<ImageView>(R.id.admin_approve_button)
                    val adminRejectButton = teacherView.findViewById<ImageView>(R.id.admin_reject_button)


                    emailText.text = email
                    //adminApprovedButton.visibility = if (isApproved === "") View.VISIBLE else View.GONE
                    //adminRejectButton.visibility = if (isApproved === "") View.VISIBLE else View.GONE

                    if (isApproved !== "") {
                        adminApprovedButton.alpha = 0.5f
                        adminApprovedButton.isEnabled = false

                        adminRejectButton.alpha = 0.5f
                        adminRejectButton.isEnabled = false
                    }

                    viewDetailsButton.setOnClickListener {
                        val intent = Intent(this, AdminViewDetails::class.java)
                        intent.putExtra("Email", email)
                        intent.putExtra("Name", name)
                        intent.putExtra("Section", section)
                        intent.putExtra("isApproved", isApproved)
                        intent.putExtra("userId", userId)


                        startActivity(intent)
                    }

                    adminApprovedButton.setOnClickListener {
                        val dialog = AlertDialog.Builder(this@AdminApprovalPage)
                            .setTitle("Confirm Approval")
                            .setMessage("Are you sure you want to approve this teacher? $email")
                            .setCancelable(false)
                            .setPositiveButton("Yes") { _, _ ->
                                firestore.collection("users")
                                    .document(document.id)
                                    .update("isApproved", "true")
                                    .addOnSuccessListener {
//                                        adminApprovedButton.visibility = View.GONE
//                                        adminRejectButton.visibility = View.GONE
                                        adminApprovedButton.alpha = 0.5f
                                        adminApprovedButton.isEnabled = false

                                        adminRejectButton.alpha = 0.5f
                                        adminRejectButton.isEnabled = false

                                        sendEmailApproved(email)
                                        Toast.makeText(this@AdminApprovalPage, "Teacher approved", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .setNegativeButton("No") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .create() // ✅ IMPORTANT

                        dialog.setOnShowListener {
                            val darkGreen = getColor(R.color.dark_green)
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                ?.setTextColor(darkGreen)
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                ?.setTextColor(darkGreen)
                        }

                        dialog.show()
                    }

                    adminRejectButton.setOnClickListener {
                        val dialog = AlertDialog.Builder(this@AdminApprovalPage)
                            .setTitle("Confirm Rejection")
                            .setMessage("Are you sure you want to reject this teacher?")
                            .setCancelable(false)
                            .setPositiveButton("Yes") { _, _ ->
                                firestore.collection("users")
                                    .document(userId)
                                    .update("isApproved", "false")
                                    .addOnSuccessListener {
//                                        adminApprovedButton.visibility = View.GONE
//                                        adminRejectButton.visibility = View.GONE
                                        adminApprovedButton.alpha = 0.5f
                                        adminApprovedButton.isEnabled = false

                                        adminRejectButton.alpha = 0.5f
                                        adminRejectButton.isEnabled = false

                                        sendEmailReject(email)
                                        Toast.makeText(this@AdminApprovalPage, "Teacher rejected", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .setNegativeButton("No") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .create() // ✅ IMPORTANT

                        dialog.setOnShowListener {
                            val darkGreen = getColor(R.color.dark_green)
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                ?.setTextColor(darkGreen)
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                ?.setTextColor(darkGreen)
                        }

                        dialog.show()
                    }

                    emailContainer.addView(teacherView)
                }
            }
            .addOnFailureListener { e ->
                Log.e("AdminPage", "Error loading teachers", e)
            }
    }

    private fun sendEmailReject(email: String) {
        Thread {
            try {
                val senderEmail = "tntsheadteacher@gmail.com"
                val appPassword = "fspbhquxnoqqnhts" // Use Gmail App Password

                val props = java.util.Properties()
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.starttls.enable"] = "true"
                props["mail.smtp.host"] = "smtp.gmail.com"
                props["mail.smtp.port"] = "587"

                val session = javax.mail.Session.getInstance(props,
                    object : javax.mail.Authenticator() {
                        override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                            return javax.mail.PasswordAuthentication(senderEmail, appPassword)
                        }
                    })

                val message = javax.mail.internet.MimeMessage(session)
                message.setFrom(javax.mail.internet.InternetAddress(senderEmail,"TNTS Head Teacher"))
                message.setRecipients(
                    javax.mail.Message.RecipientType.TO,
                    javax.mail.internet.InternetAddress.parse(email)
                )

                message.subject = "Update Regarding Your Email Submission"
                message.setText(
                    """
                Good day,

                This is the Head Teacher from the CSS App. We regret to inform you that your submitted email has not been approved at this time.

                If you believe this was made in error or if you require further clarification, please feel free to contact us for additional assistance.

                Thank you for your understanding.

                Kind regards,
                CSS App Head Teacher
                """.trimIndent()
                )

                javax.mail.Transport.send(message)

                runOnUiThread {
                    //Toast.makeText(this@AdminPage, "Email sent successfully, $email", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    //Toast.makeText(this@AdminPage, "Failed to send email: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun sendEmailApproved(email: String) {
        Thread {
            try {
                val senderEmail = "tntsheadteacher@gmail.com"
                val appPassword = "fspbhquxnoqqnhts" // Use Gmail App Password

                val props = java.util.Properties()
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.starttls.enable"] = "true"
                props["mail.smtp.host"] = "smtp.gmail.com"
                props["mail.smtp.port"] = "587"

                val session = javax.mail.Session.getInstance(props,
                    object : javax.mail.Authenticator() {
                        override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                            return javax.mail.PasswordAuthentication(senderEmail, appPassword)
                        }
                    })

                val message = javax.mail.internet.MimeMessage(session)
                message.setFrom(javax.mail.internet.InternetAddress(senderEmail,"TNTS Head Teacher"))
                message.setRecipients(
                    javax.mail.Message.RecipientType.TO,
                    javax.mail.internet.InternetAddress.parse(email)
                )

                message.subject = "Update Regarding Your Email Submission"
                message.setText(
                    """
                Good day,

                This is the Head Teacher from the CSS App. We are pleased to inform you that your submitted email has been successfully approved.
                
                You may now access your teacher account and use the available features within the application.
                
                If you have any questions or require further assistance, please feel free to contact us.
                
                Thank you for your cooperation.

                Kind regards,
                CSS App Head Teacher
                """.trimIndent()
                )

                javax.mail.Transport.send(message)

                runOnUiThread {
                    //Toast.makeText(this@AdminPage, "Email sent successfully, $email", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    //Toast.makeText(this@AdminPage, "Failed to send email: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }


}