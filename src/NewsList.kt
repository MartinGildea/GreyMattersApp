package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot


class NewsList : BaseActivity() {
    private val mImageAddresses = ArrayList<String>()
    private val mTitles = ArrayList<String>()
    private val mDates = ArrayList<String>()
    private val mSummaries = ArrayList<String>()
    private val mDocuments = ArrayList<DocumentSnapshot>()
    private val mNewsLinks = ArrayList<String>()
    private val mColRef = FirebaseFirestore.getInstance().collection("news").orderBy("Date")

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivityTitle = "Recent News"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newslist)
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
                    reportError("NEWS LIST ERROR: " + "UNABLE_TO_CONNECT_TO_EVENTS_DATABASE")
                }
            }
            .addOnFailureListener { exception ->
                val e = exception as FirebaseAuthException
                reportError("NEWS LIST ERROR: " + e.errorCode)
            }
    }

    private fun getDataFromCloud(documentSnapshot: DocumentSnapshot) {
        val date = documentSnapshot.getTimestamp("Date")!!.toDate()
        val dateFormat = SimpleDateFormat("dd-MM-yy")
        mDates.add(dateFormat.format(date))
        documentSnapshot.getString("ImageAddress")?.let { mImageAddresses.add(it) }
        documentSnapshot.getString("Title")?.let { mTitles.add(it) }
        documentSnapshot.getString("Summary")?.let { mSummaries.add(it) }
        documentSnapshot.getString("Link")?.let { mNewsLinks.add(it) }
        mDocuments.add(documentSnapshot)
    }

    private fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = RecyclerViewAdapter(this, mImageAddresses, mTitles, mDates, mSummaries, mDocuments, "News", mNewsLinks)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}




