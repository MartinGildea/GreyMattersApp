package com.GreyMatters.GreyMattersApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseUser




class MainActivity : BaseActivity() {

    private lateinit var mSignInButton: Button
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = "LogIn"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mSignInButton = findViewById(R.id.sign_in_button)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "Logged In")
                val intent = Intent(this, Home::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                Toast.makeText(this, "Successfully signed in with " + user.email, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Log.d(TAG, "Logged out")
            }
        }

        mSignInButton.setOnClickListener {
            val email = mEmail.text.toString()
            val password = mPassword.text.toString()
            if(!connectedToInternet()) {
                Toast.makeText(this, "You are not connected to the internet. Please verify your conenction and try again.", Toast.LENGTH_SHORT).show()
            } else if(email.equals("") && password.equals("")){
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
            }
        }
    }
}
