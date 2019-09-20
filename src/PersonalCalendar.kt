package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import android.view.View
import android.app.Dialog
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.text.SimpleDateFormat

import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuthException


class PersonalCalendar : BaseActivity() {

    private val mEvents = ArrayList<EventDay>()

    private var mEventHashMap = HashMap<String, Timestamp>()
    private var mImageHashMap = HashMap<String, String>()

    private val mImageAddresses = ArrayList<String>()
    private val mTitles = ArrayList<String>()
    private val mDates = ArrayList<String>()
    private val mSummaries = ArrayList<String>()
    private val mDocuments = ArrayList<DocumentSnapshot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivityTitle = "Personal Calendar"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_calendar)
        initFirebaseAuthentication()

        val user = mAuth.currentUser
        if(user != null) {
            val docRef = FirebaseFirestore.getInstance().collection("users").document(user.email!!)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    mEventHashMap = HashMap()
                    mImageHashMap = HashMap()
                    if ((document.data?.get("EventDates") != null) and (document.data?.get("EventIcons") != null)) {
                        mEventHashMap = document.data?.get("EventDates") as HashMap<String, Timestamp>
                        mImageHashMap = document.data?.get("EventIcons") as HashMap<String, String>
                    }
                    getEvents(mEventHashMap, mImageHashMap)
                }
            }
        }

        val calendarView = findViewById<View>(R.id.personalCalendar) as CalendarView
        calendarView.setOnDayClickListener { eventDay ->
            clearRecyclerArrays()
            initEventList(eventDay.calendar.time.time, mEventHashMap)
        }
    }



    private fun getEvents(eventHashMap: HashMap<String, Timestamp>, imageHashMap: HashMap<String, String>) {
        val calendarView = findViewById<View>(R.id.personalCalendar) as CalendarView
        calendarView.setEvents(mEvents)
        for (entry in eventHashMap) {
            val calendar = Calendar.getInstance()
            val date = entry.value.toDate()
            calendar.time = date
            var logo = R.mipmap.grey_matters_logo
            when {
                imageHashMap[entry.key] == "Grey Matters" -> logo = R.drawable.grey_matters_event_logo
                imageHashMap[entry.key] == "Local Vocals" -> logo = R.drawable.local_vocals_logo
                imageHashMap[entry.key] == "Art Group" -> logo = R.drawable.art_group_logo
                imageHashMap[entry.key] == "Game On" -> logo = R.drawable.game_on_logo
                imageHashMap[entry.key] == "Knitting Bee" -> logo = R.drawable.knitting_bee_logo
            }
            mEvents.add(EventDay(calendar, logo))
        }
    }


    private fun initEventList(date: Long, eventHashMap: HashMap<String, Timestamp>) {
        val colRef = FirebaseFirestore.getInstance().collection("events")
            .orderBy("Date")
            .whereGreaterThan("Date",  Timestamp(date/1000-1, 0))
            .whereLessThan("Date",  Timestamp(date/1000+86400, 0))

        colRef.get()
            .addOnSuccessListener { collection ->
                if (collection != null) {
                    var idMatch = false
                    val documentList = ArrayList<DocumentSnapshot>()
                    for (document in collection.documents) {
                        if (eventHashMap.containsKey(document.id)) {
                            idMatch = true
                            documentList.add(document)
                        }
                    }
                    if (idMatch) {
                        val dialog = Dialog(this)
                        dialog.setContentView(R.layout.alertdialog_calendar_events)
                        dialog.setTitle("Title")
                        val recyclerView = dialog.findViewById<RecyclerView>(R.id.calendarRecyclerView)
                        for (document in documentList) {
                            getDataFromCloud(document)
                            initRecyclerView(recyclerView)
                        }
                        val dialogButton = dialog.findViewById(R.id.calendarDialogButtonOK) as Button
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                } else {
                    val confirmLogOutDialog = AlertDialog.Builder(this)
                    confirmLogOutDialog.setMessage("An unknown Error has occurred. Our servers may be down." +
                            "If problems persist, please contact GreyMatters and quote the following error message:\n" +
                            "CALENDAR ERROR: " + "UNABLE_TO_CONNECT_TO_EVENTS_DATABASE")
                    confirmLogOutDialog.setPositiveButton("Ok,"){_, _  ->
                    }
                    val dialog: AlertDialog = confirmLogOutDialog.create()
                    dialog.show()
                }
            }
            .addOnFailureListener { exception ->
                val e = exception as FirebaseAuthException
                val confirmLogOutDialog = AlertDialog.Builder(this)
                confirmLogOutDialog.setMessage("An unknown Error has occurred. Our servers may be down." +
                        "If problems persist, please contact GreyMatters and quote the following error message:\n" +
                        "CALENDAR ERROR: " + e.errorCode)
                confirmLogOutDialog.setPositiveButton("Ok,"){_, _  ->
                }
                val dialog: AlertDialog = confirmLogOutDialog.create()
                dialog.show()
            }
    }

    private fun clearRecyclerArrays(){
        mImageAddresses.clear()
        mTitles.clear()
        mDates.clear()
        mSummaries.clear()
        mDocuments.clear()
    }

    private fun initRecyclerView(recyclerView: RecyclerView){
        val newsLinks: ArrayList<String> = ArrayList()
        val adapter = RecyclerViewAdapter(this, mImageAddresses, mTitles, mDates, mSummaries, mDocuments, "EventCalendar", newsLinks)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getDataFromCloud(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.getString("ImageAddress")?.let { mImageAddresses.add(it) }
        documentSnapshot.getString("Title")?.let { mTitles.add(it) }
        val date = documentSnapshot.getTimestamp("Date")!!.toDate()
        val dateFormat = SimpleDateFormat("dd-MM-yy HH:mm")
        mDates.add(dateFormat.format(date))
        documentSnapshot.getString("Summary")?.let { mSummaries.add(it) }
        mDocuments.add(documentSnapshot)
    }
}
