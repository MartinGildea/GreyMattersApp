package com.GreyMatters.GreyMattersApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import java.util.ArrayList


class EventOverview : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = "EventOverview"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_overview)

        initFirebaseAuthentication()

        val intent = intent
        val backgroundImageAddress = intent.getStringExtra("wide_image_address")
        val backgroundImage: ImageView = findViewById(R.id.eventBackgroundImage) as ImageView
        Glide.with(this)
            .load(backgroundImageAddress)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(backgroundImage)

        val titleString = intent.getStringExtra("title")
        val title: TextView = findViewById(R.id.eventTitle) as TextView
        title.text = titleString

        val dateString = intent.getStringExtra("date")
        val date: TextView = findViewById(R.id.eventDate) as TextView
        date.text = dateString

        val locationString = intent.getStringExtra("location")
        val location: TextView = findViewById(R.id.eventLocation) as TextView
        location.text = locationString

        val eventText = intent.getStringExtra("text")
        val text: TextView = findViewById(R.id.eventText) as TextView
        text.text = eventText
    }
}
