package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.google.firebase.firestore.FirebaseFirestore


class ContactUs : BaseActivity() {
    private val mColRef = FirebaseFirestore.getInstance().collection("messages")


    override fun onCreate(savedInstanceState: Bundle?) {
        mActivityTitle = "Contact Us"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        initFirebaseAuthentication()

        val contactUsName = findViewById<EditText>(R.id.contact_us_name)
        val contactUsSubject = findViewById<EditText>(R.id.contact_us_subject)
        val contactUsText = findViewById<EditText>(R.id.contact_us_text)
        val  sendMessage = findViewById<Button>(R.id.sendMessage)

        sendMessage.setOnClickListener {
            val name = contactUsName.text.toString()
            val subject = contactUsSubject.text.toString()
            val text = contactUsText.text.toString()
            when {
                (name == "") or (subject == "") or (text == "") -> {
                    Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                }
                name.length > 30 -> {
                    Toast.makeText(this, "Please ensure name is no longer than 30 characters.", Toast.LENGTH_SHORT).show()
                }
                subject.length > 100 -> {
                    Toast.makeText(this, "Please ensure subject is no longer than 100 characters.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    sendMessage(name, subject, text)
                }
            }
        }

    }


    private fun sendMessage(name:String, subject:String, text:String){

        if (connectedToInternet()) {
            val newMessage = hashMapOf(
                "name" to name,
                "subject" to subject,
                "text" to text
            )
            mColRef
                .add(newMessage)
                .addOnSuccessListener {
                    val confirmLogOutDialog = AlertDialog.Builder(this)
                    confirmLogOutDialog.setMessage("Message Successfully Sent!")
                    confirmLogOutDialog.setPositiveButton("Okay") { _, _ -> }
                    val dialog: AlertDialog = confirmLogOutDialog.create()
                    dialog.show()
                }
                .addOnFailureListener { _ ->
                    val confirmLogOutDialog = AlertDialog.Builder(this)
                    confirmLogOutDialog.setMessage("Error sending message. There may be connectivity issues." +
                            "\n Please verify your connection and try again later.")
                    confirmLogOutDialog.setPositiveButton("Okay") { _, _ -> }
                    val dialog: AlertDialog = confirmLogOutDialog.create()
                    dialog.show()
                }
        } else {
            Toast.makeText(this, "You are not connected to the internet. Please verify your connection and try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
