package com.GreyMatters.GreyMattersApp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException


class LogIn : BaseActivity() {

    private lateinit var mSignInButton: Button
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mRegisterAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivityTitle = "Log In"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        mAuth = FirebaseAuth.getInstance()
        mSignInButton = findViewById(R.id.sign_in_button)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)
        mRegisterAccount = findViewById(R.id.register_link)

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                val intent = Intent(this, Home::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                Toast.makeText(this, "Successfully signed in with " + user.email, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        mSignInButton.setOnClickListener {
            val email = mEmail.text.toString()
            val password = mPassword.text.toString()
            if(!connectedToInternet()) {
                Toast.makeText(this, "You are not connected to the internet. Please verify your connection and try again.", Toast.LENGTH_SHORT).show()
            } else if((email == "") or (password == "")){
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        val e = task.exception as FirebaseAuthException
                        when {
                            e.errorCode == "ERROR_INVALID_EMAIL" -> {
                                Toast.makeText(baseContext, "Please ensure you have entered a valid email address.", Toast.LENGTH_SHORT).show()
                            }
                            (e.errorCode == "ERROR_USER_NOT_FOUND") or (e.errorCode == "ERROR_WRONG_PASSWORD") -> {
                                Toast.makeText(baseContext, "Incorrect details entered. Please try again.",
                                    Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                reportError("LOG IN ERROR: " + e.errorCode)
                            }
                        }
                    }
                }
            }
        }

        mRegisterAccount.setOnClickListener {
            val confirmLogOutDialog = AlertDialog.Builder(this)
            val privacyStatement= getString(R.string.privacy_statement)
            confirmLogOutDialog.setMessage(privacyStatement)
            confirmLogOutDialog.setPositiveButton("I Accept"){_, _  ->
                startActivity(Intent(this, RegisterAccount::class.java))
            }
            confirmLogOutDialog.setNegativeButton("Back"){_, _  -> }
            val dialog: AlertDialog = confirmLogOutDialog.create()
            dialog.show()
        }
    }
}
