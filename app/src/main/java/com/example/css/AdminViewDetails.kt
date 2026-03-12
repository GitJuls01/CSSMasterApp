package com.example.css

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class AdminViewDetails : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_view_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()

        val backButton = findViewById<ImageButton>(R.id.back_button)
        val emailText = findViewById<TextView>(R.id.text_email)
        val nameText = findViewById<TextView>(R.id.text_name)
        val sectionText = findViewById<TextView>(R.id.teacher_section)
        val approvedButton = findViewById<ImageView>(R.id.admin_approve_button)
        val rejectButton = findViewById<ImageView>(R.id.admin_reject_button)

        val email = intent.getStringExtra("Email") ?: "No Email"
        val name = intent.getStringExtra("Name")
        val section = intent.getStringExtra("Section")
        val isApproved = intent.getStringExtra("isApproved")
        val userId = intent.getStringExtra("userId")



        emailText.text = getString(R.string.email1_label, email)
        nameText.text = getString(R.string.name1_label, name)
        sectionText.text = getString(R.string.section_label, section)

//        approvedButton.visibility =
//            if (isApproved == "") View.VISIBLE else View.GONE
//        rejectButton.visibility =
//            if (isApproved == "") View.VISIBLE else View.GONE

        if (isApproved !== "") {
            approvedButton.alpha = 0.5f
            approvedButton.isEnabled = false
            rejectButton.alpha = 0.5f
            rejectButton.isEnabled = false
        }
        backButton.setOnClickListener {
            val intent = Intent(this, AdminPage::class.java)
            startActivity(intent)
        }

        approvedButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this@AdminViewDetails)
                .setTitle("Confirm Approval")
                .setMessage("Are you sure you want to approve this teacher?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    if (userId != null) {
                        firestore.collection("users")
                            .document(userId)
                            .update("isApproved", "true")
                            .addOnSuccessListener {
//                                approvedButton.visibility = View.GONE
//                                rejectButton.visibility = View.GONE

                                approvedButton.alpha = 0.5f
                                approvedButton.isEnabled = false

                                rejectButton.alpha = 0.5f
                                rejectButton.isEnabled = false

                                sendEmailApproved(email)
                                Toast.makeText(this@AdminViewDetails, "Teacher approved", Toast.LENGTH_SHORT).show()
                            }
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

        rejectButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this@AdminViewDetails)
                .setTitle("Confirm Rejection")
                .setMessage("Are you sure you want to reject this teacher?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    if (userId != null) {
                        firestore.collection("users")
                            .document(userId)
                            .update("isApproved", "false")
                            .addOnSuccessListener {
//                                approvedButton.visibility = View.GONE
//                                rejectButton.visibility = View.GONE

                                approvedButton.alpha = 0.5f
                                approvedButton.isEnabled = false

                                rejectButton.alpha = 0.5f
                                rejectButton.isEnabled = false

                                sendEmailReject(email)
                                Toast.makeText(this@AdminViewDetails, "Teacher rejected", Toast.LENGTH_SHORT).show()
                            }
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