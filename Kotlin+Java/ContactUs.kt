package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ContactUs : BaseActivity() {
    private val mColRef = FirebaseFirestore.getInstance().collection("messages")
    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = "ContactUs"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        initFirebaseAuthentication()

        val contactUsName = findViewById(R.id.contact_us_name) as EditText
        val contactUsSubject = findViewById(R.id.contact_us_subject) as EditText
        val contactUsText = findViewById(R.id.contact_us_text) as EditText


        val  sendMessage = findViewById(R.id.sendMessage) as Button
        sendMessage.setOnClickListener {
            if (connectedToInternet()) {
                val newMessage = hashMapOf(
                    "name" to contactUsName.text.toString(),
                    "subject" to contactUsSubject.text.toString(),
                    "text" to contactUsText.text.toString()
                )
                mColRef
                    .add(newMessage)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully written!")
                        val confirmLogOutDialog = AlertDialog.Builder(this)
                        confirmLogOutDialog.setMessage("Message Successfully Sent!")
                        confirmLogOutDialog.setPositiveButton("Okay") { _, _ -> }
                        val dialog: AlertDialog = confirmLogOutDialog.create()
                        dialog.show()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error writing document", e)
                        val confirmLogOutDialog = AlertDialog.Builder(this)
                        confirmLogOutDialog.setMessage("Error sending message. There may be connectivity issues." +
                                "\n Please verify your connection and try again later.")
                        confirmLogOutDialog.setPositiveButton("Okay") { _, _ -> }
                        val dialog: AlertDialog = confirmLogOutDialog.create()
                        dialog.show()
                    }
            } else {
                Toast.makeText(this, "You are not connected to the internet. Please verify your conenction and try again.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
