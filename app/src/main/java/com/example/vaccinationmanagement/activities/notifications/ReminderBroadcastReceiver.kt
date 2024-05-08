package com.example.vaccinationmanagement.activities.notifications
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.vaccinationmanagement.R

/*
This class is a broadcast receiver that triggers a notification
when it receives a broadcast. It will used to remind the user of an upcoming appointment.
 */


class ReminderBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val appointmentId = intent.getIntExtra("appointment_id", -1)
        if (appointmentId != -1) {
            val builder = NotificationCompat.Builder(context, "reminder_channel")
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle("Appointment Reminder")
                .setContentText("You have an appointment scheduled.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(context)) {
                notify(appointmentId, builder.build())
            }
        }
    }
}