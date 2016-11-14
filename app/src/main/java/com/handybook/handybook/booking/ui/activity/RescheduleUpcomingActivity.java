package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.R;

/**
 * This displays a list of future bookings that is qualified to be rescheduled. Used in the context
 * of a pro team conversation
 */
public class RescheduleUpcomingActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_upcoming);
    }
}
