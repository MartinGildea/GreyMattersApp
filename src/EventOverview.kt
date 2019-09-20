package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import java.text.SimpleDateFormat

import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class EventOverview : BaseActivity() {

    private lateinit var addToUserEventsButton: Button
    private lateinit var mCalendarStatusTextView: TextView
    private lateinit var mDateString: String
    private lateinit var mTitleString: String
    private var mButtonType = -1
    private val mDateFormat = SimpleDateFormat("dd-MM-yy HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivityTitle = "Event Overview"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_overview)
        addToUserEventsButton = findViewById(R.id.eventButton)
        mCalendarStatusTextView = findViewById(R.id.calendarStatus)

        initFirebaseAuthentication()

        val intent = intent
        val backgroundImageAddress = intent.getStringExtra("wide_image_address")
        val backgroundImage: ImageView = findViewById(R.id.eventBackgroundImage)
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(this)
            .load(backgroundImageAddress)
            .placeholder(circularProgressDrawable)
            .into(backgroundImage)

        mTitleString = intent.getStringExtra("title")
        val title: TextView = findViewById(R.id.eventTitle)
        title.text = mTitleString

        mDateString = intent.getStringExtra("date")
        val date: TextView = findViewById(R.id.eventDate)
        date.text = mDateString

        val locationString = intent.getStringExtra("location")
        val location: TextView = findViewById(R.id.eventLocation)
        location.text = locationString

        val eventText = intent.getStringExtra("text")
        val text: TextView = findViewById(R.id.eventText)
        text.text = eventText

        setButtonType(false)
        addToUserEventsButton.setOnClickListener {
            setButtonType(true)
        }
    }

    private fun setButtonType(buttonClicked: Boolean) {
        val user = mAuth.currentUser
        if (user != null) {
            val docRef = FirebaseFirestore.getInstance().collection("users").document(user.email!!)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        var dateHashMap: HashMap<String, Timestamp> = HashMap()
                        var imageHashMap: HashMap<String, String> = HashMap()
                        if (document.data?.get("EventDates") != null) {
                            dateHashMap = document.data!!["EventDates"] as HashMap<String, Timestamp>
                            imageHashMap = document.data!!["EventIcons"] as HashMap<String, String>
                        }
                        val id = intent.getStringExtra("id")
                        if (dateHashMap.containsKey(id)){
                            mButtonType = 1
                            if (buttonClicked){
                                modifyUserEvents(docRef, id, dateHashMap, imageHashMap)
                            }
                            updateUI()
                        } else {
                            mButtonType = 0
                            if (buttonClicked){
                                modifyUserEvents(docRef, id, dateHashMap, imageHashMap)
                            }
                            updateUI()
                        }
                    } else {
                        mButtonType = -1
                        updateUI()
                    }
                }
                .addOnFailureListener {
                    mButtonType = -1
                    updateUI()
                }
        } else {
            mButtonType = -1
            updateUI()
        }
    }

    private fun updateUI() {
        when (mButtonType) {
            -1 -> {
                addToUserEventsButton.isVisible = false
                mCalendarStatusTextView.isVisible = false
                addToUserEventsButton.setText(R.string.add_to_users_events)
            }
            0 -> {
                addToUserEventsButton.isVisible = true
                mCalendarStatusTextView.isVisible = false
                addToUserEventsButton.setText(R.string.add_to_users_events)
            }
            else -> {
                addToUserEventsButton.isVisible = true
                mCalendarStatusTextView.isVisible = true
                addToUserEventsButton.setText(R.string.remove_from_users_events)
            }
        }
    }

    private fun modifyUserEvents(documentReference: DocumentReference, id: String, eventDateHashMap: HashMap<String, Timestamp>, eventIconHashMap: HashMap<String, String>){
        if (mButtonType == 0) {
            eventDateHashMap[id] = Timestamp((mDateFormat.parse(mDateString)))
            eventIconHashMap[id] = mTitleString
            documentReference.update("EventDates", eventDateHashMap)
            documentReference.update("EventIcons", eventIconHashMap)
            mButtonType = 1
        } else {
            eventDateHashMap.remove(id)
            eventIconHashMap.remove(id)
            documentReference.update("EventDates", eventDateHashMap)
            documentReference.update("EventIcons", eventIconHashMap)
            mButtonType = 0
        }
    }
}
