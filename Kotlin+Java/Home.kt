package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class Home : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = "Home"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initFirebaseAuthentication()

        val homeGoToEvents = findViewById(R.id.homeGoToEvents) as Button
        homeGoToEvents.setOnClickListener {
            startActivity(Intent(this, EventList::class.java))
        }

        val homeGoToNews = findViewById(R.id.homeGoToNews) as Button
        homeGoToNews.setOnClickListener {
            startActivity(Intent(this, NewsList::class.java))
        }

        val homeGoToContactUs = findViewById(R.id.homeGoToContactUs) as Button
        homeGoToContactUs.setOnClickListener {
            startActivity(Intent(this, ContactUs::class.java))
        }

        val homeGoToPersonalCalendar = findViewById(R.id.homeGoToPersonalCalendar) as Button
        homeGoToPersonalCalendar.setOnClickListener {
            startActivity(Intent(this, PersonalCalendar::class.java))
        }
    }


}
