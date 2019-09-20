package com.GreyMatters.GreyMattersApp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var actionBar: Menu
    protected lateinit var mAuth: FirebaseAuth
    protected lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    protected lateinit var mActivityTitle: String

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        actionBar = menu
        menuInflater.inflate(R.menu.logged_out_action_bar, menu)
        updateUI(mAuth.currentUser)

        val actionBar = supportActionBar
        actionBar!!.setHomeButtonEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.home_icon)
        actionBar.title = mActivityTitle
        actionBar.show()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_in_button -> {
                startActivity(Intent(this, LogIn::class.java))
                return true
            }

            android.R.id.home -> {
                startActivity(Intent(this, Home::class.java))
                return true
            }

            R.id.sign_out_button -> {
                val confirmLogOutDialog = AlertDialog.Builder(this)
                confirmLogOutDialog.setMessage("Are you want to log out? This will return you to the home screen.")
                confirmLogOutDialog.setPositiveButton("Yes"){_, _  ->
                    mAuth.signOut()
                    val intent = Intent(this, Home::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                confirmLogOutDialog.setNegativeButton("No"){_, _ -> }
                val dialog: AlertDialog = confirmLogOutDialog.create()
                dialog.show()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    public override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(mAuthListener)
    }

    private fun updateUI(user: FirebaseUser?) {
        val signInButton = actionBar.findItem(R.id.sign_in_button)
        val signOutButton = actionBar.findItem(R.id.sign_out_button)
        if (user != null) {
            signInButton.isVisible = false
            signOutButton.isVisible = true
        } else {
            signInButton.isVisible = true
            signOutButton.isVisible = false
        }
    }

    fun initFirebaseAuthentication(){
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { }
    }

    fun connectedToInternet(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }

    fun reportError(error: String) {
        val confirmLogOutDialog = AlertDialog.Builder(this)
        confirmLogOutDialog.setMessage("An unknown Error has occurred. Our servers may be down." +
                "If problems persist, please contact GreyMatters and quote the following error message:\n" +
                error)
        confirmLogOutDialog.setPositiveButton("Ok,"){_, _  ->
        }
        val dialog: AlertDialog = confirmLogOutDialog.create()
        dialog.show()
    }
}