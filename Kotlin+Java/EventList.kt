package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat


class EventList : BaseActivity() {
    private val mImageAddresses = ArrayList<String>()
    private val mTitles = ArrayList<String>()
    private val mDates = ArrayList<String>()
    private val mSummaries = ArrayList<String>()
    private val mDocuments = ArrayList<DocumentSnapshot>()
    private val mColRef = FirebaseFirestore.getInstance().collection("events").orderBy("Date").whereGreaterThan("Date",  Timestamp(System.currentTimeMillis() / 1000, 0))


    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = "EventList"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventlist)

        initFirebaseAuthentication()

        FirebaseApp.initializeApp(this)
        initEventList()
    }

    fun handleCloudFirestore(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.getString("ImageAddress")?.let { mImageAddresses.add(it) }
        documentSnapshot.getString("Title")?.let { mTitles.add(it) }
        val date = documentSnapshot.getTimestamp("Date")!!.toDate()
        val dateFormat = SimpleDateFormat("dd-MM-yy HH:mm")
        mDates.add(dateFormat.format(date))
        documentSnapshot.getString("Summary")?.let { mSummaries.add(it) }
        mDocuments.add(documentSnapshot)
        initRecyclerView()
    }

    private fun initEventList() {
        mColRef.get()
            .addOnSuccessListener { collection ->
                if (collection != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${collection.documents[0]}")
                    for (document in collection.documents){
                        handleCloudFirestore(document)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = RecyclerViewAdapter(this, mImageAddresses, mTitles, mDates, mSummaries, mDocuments, "Event")
        recyclerView.setAdapter(adapter)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}




