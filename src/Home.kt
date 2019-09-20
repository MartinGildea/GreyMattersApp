package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout


class Home : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivityTitle = "Home"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initFirebaseAuthentication()

        val homeGoToEvents = findViewById<ConstraintLayout>(R.id.homeGoToEvents)
        homeGoToEvents.setOnClickListener {
            startActivity(Intent(this, EventList::class.java))
        }

        val homeGoToNews = findViewById<ConstraintLayout>(R.id.homeGoToNews)
        homeGoToNews.setOnClickListener {
            startActivity(Intent(this, NewsList::class.java))
        }

        val homeGoToVolunteering = findViewById<ConstraintLayout>(R.id.homeGoToVolunteering)
        homeGoToVolunteering.setOnClickListener {
            startActivity(Intent(this, VolunteeringList::class.java))
        }

        val homeGoToContactUs = findViewById<ConstraintLayout>(R.id.homeGoToContactUs)
        homeGoToContactUs.setOnClickListener {
            startActivity(Intent(this, ContactUs::class.java))
        }

        val homeGoToPersonalCalendar = findViewById<ConstraintLayout>(R.id.homeGoToPersonalCalendar)
        val personalCalendarText = findViewById<TextView>(R.id.calendarButtonText)
        val homeGoToAccountManager = findViewById<ConstraintLayout>(R.id.homeGoToAccount)
        val manageAccountText = findViewById<TextView>(R.id.manageAccountText)
        val user = mAuth.currentUser
        if (user != null) {
            homeGoToPersonalCalendar.setOnClickListener {
                startActivity(Intent(this, PersonalCalendar::class.java))
            }

            homeGoToAccountManager.setOnClickListener {
                startActivity(Intent(this, AccountManager::class.java))
            }
        } else {
            homeGoToPersonalCalendar.setBackgroundColor(Color.parseColor("#EEEEEE"))
            personalCalendarText.setBackgroundColor(Color.parseColor("#E4E4E4"))

            homeGoToAccountManager.setBackgroundColor(Color.parseColor("#EEEEEE"))
            manageAccountText.setBackgroundColor(Color.parseColor("#E4E4E4"))

            homeGoToPersonalCalendar.setOnClickListener {
                Toast.makeText(this, "Please log in to use this functionality.", Toast.LENGTH_SHORT).show()

            }

            homeGoToAccountManager.setOnClickListener {
                Toast.makeText(this, "Please log in to use this functionality.", Toast.LENGTH_SHORT).show()

            }
        }
    }


}
