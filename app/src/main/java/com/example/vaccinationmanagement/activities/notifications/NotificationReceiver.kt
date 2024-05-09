
package com.example.vaccinationmanagement.activities.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val appointmentId = intent.getIntExtra("appointment_id", -1)
        if (appointmentId != -1) {
        }
    }
}