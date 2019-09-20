package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot


class VolunteeringList : BaseActivity() {
    private val mImageAddresses = ArrayList<String>()
    private val mTitles = ArrayList<String>()
    private val mDates = ArrayList<String>()
    private val mSummaries = ArrayList<String>()
    private val mDocuments = ArrayList<DocumentSnapshot>()
    private val mColRef = FirebaseFirestore.getInstance().collection("volunteering")

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivityTitle = "Volunteering"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteeringlist)
        initFirebaseAuthentication()
        FirebaseApp.initializeApp(this)
        initEventList()
    }

    private fun initEventList() {
        mColRef.get()
            .addOnSuccessListener { collection ->
                if (collection != null) {
                    for (document in collection.documents){
                        getDataFromCloud(document)
                        initRecyclerView()
                    }
                } else {
                    reportError("VOLUNTEERING LIST ERROR: " + "UNABLE_TO_CONNECT_TO_EVENTS_DATABASE")
                }
            }
            .addOnFailureListener { exception ->
                val e = exception as FirebaseAuthException
                reportError("VOLUNTEERING LIST ERROR: " + e.errorCode)
            }
    }

    private fun getDataFromCloud(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.getString("ImageAddress")?.let { mImageAddresses.add(it) }
        documentSnapshot.getString("Title")?.let { mTitles.add(it) }
        val date = documentSnapshot.getTimestamp("Date")?.toDate()
        val dateFormat = SimpleDateFormat("dd-MM-yy HH:mm")
        if (date != null) {
            mDates.add(dateFormat.format(date))
        }
        documentSnapshot.getString("Summary")?.let { mSummaries.add(it) }
        mDocuments.add(documentSnapshot)
    }

    private fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val newsLinks: ArrayList<String> = ArrayList()
        val adapter = RecyclerViewAdapter(this, mImageAddresses, mTitles, mDates, mSummaries, mDocuments, "News", newsLinks)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val text  = findViewById<TextView>(R.id.text_no_volunteering)
        if(adapter.itemCount != 0){
            text.visibility = View.INVISIBLE
        } else {
            text.visibility = View.VISIBLE
        }
    }
}




