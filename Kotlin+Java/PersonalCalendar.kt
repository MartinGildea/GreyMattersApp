package com.GreyMatters.GreyMattersApp

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import java.util.*


class PersonalCalendar : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = "PersonalCalendar"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_calendar)
        initFirebaseAuthentication()

        val calendar = findViewById(R.id.personalCalendar) as CalendarView
        calendar.setDate(Calendar.getInstance().getTimeInMillis(),false,true)
    }
}
