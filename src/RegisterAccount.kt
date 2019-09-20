package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.content.Intent

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterAccount : BaseActivity() {

    private val mColRef = FirebaseFirestore.getInstance().collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivityTitle = "Register"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        handleRegisteryAuth()

        val registerButton = findViewById<Button>(R.id.register_button)
        val nameEditText = findViewById<EditText>(R.id.register_name)
        val emailEditText = findViewById<EditText>(R.id.register_email)
        val passwordEditText = findViewById<EditText>(R.id.register_password)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirm_password)

        registerButton.setOnClickListener {
            val nameString = nameEditText.text.toString()
            val emailString = emailEditText.text.toString()
            val passwordString = passwordEditText.text.toString()
            val confirmPasswordString = confirmPasswordEditText.text.toString()

            if(!connectedToInternet()) {
                Toast.makeText(this, "You are not connected to the internet. Please verify your connection and try again.", Toast.LENGTH_SHORT).show()
            } else if((nameString == "") or (emailString == "") or (passwordString == "") or (confirmPasswordString == "")){
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            } else if(passwordString != confirmPasswordString){
                Toast.makeText(this, "Your passwords do not match. Please re-enter your password.", Toast.LENGTH_SHORT).show()
            } else {
                createUser(nameString, emailString, passwordString)
            }
        }
    }

    fun handleRegisteryAuth(){
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                val intent = Intent(this, Home::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun createUser(name: String, email: String, password: String){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val data = hashMapOf(
                    "Name" to name,
                    "EventDates" to HashMap<String, Timestamp>(),
                    "EventIcons" to HashMap<String, String>()
                )
                mColRef.document(email.toLowerCase()).set(data)
                Toast.makeText(baseContext, "Account created successfully. Logging in.", Toast.LENGTH_SHORT).show()
            } else {
                val e = task.exception as FirebaseAuthException
                reportLogInProblemToUser(e.errorCode)
            }
        }
    }
    
    private fun reportLogInProblemToUser(error: String) {
        when (error) {
            "ERROR_INVALID_EMAIL" -> {
                Toast.makeText(baseContext, "Please ensure you have entered a valid email address.", Toast.LENGTH_SHORT)
                    .show()
            }
            "ERROR_WEAK_PASSWORD" -> {
                Toast.makeText(
                    baseContext,
                    "Please ensure your password has at least 6 characters.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                Toast.makeText(
                    baseContext, "This email is already in use." +
                            "Please return to the log in page if you are trying to log in.", Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                reportError("REGISTRATION ERROR:$error")
            }
        }
    }
}
