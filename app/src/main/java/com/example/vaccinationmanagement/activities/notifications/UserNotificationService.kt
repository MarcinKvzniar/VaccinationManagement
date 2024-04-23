// UserNotificationService.kt
package com.example.vaccinationmanagement.activities.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.vaccinationmanagement.appointments.Appointments
import com.example.vaccinationmanagement.appointments.AppointmentsDAO
import com.example.vaccinationmanagement.appointments.ReminderBroadcastReceiver
import java.util.*

class UserNotificationService(private val context: Context, private val appointmentsDAO: AppointmentsDAO) {

    val notificationHelper = NotificationHelper(context)

    fun scheduleNotifications() {
        val appointments = appointmentsDAO.getAllAppointments()
        appointments?.forEach { appointment ->
            if (appointment != null && appointment.notificationPreferences != null) {
                scheduleNotification(appointment)
            }
        }
    }

    private fun scheduleNotification(appointment: Appointments) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("appointment_id", appointment.id)
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAtMillis = getTriggerAtMillis(appointment) // calculate the time at which the notification should be triggered

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appointment.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)

        // Build and display the notification immediately
        val notification = notificationHelper.buildNotification(
            "Vaccination Reminder",
            "You have a vaccination appointment coming up!"
        )
        notificationHelper.notify(appointment.id, notification)
    }

    fun cancelNotifications() {
        val appointments = appointmentsDAO.getAllAppointments()
        appointments?.forEach { appointment ->
            if (appointment != null) {
                notificationHelper.cancelNotification(appointment)
            }
        }
    }

    private fun getTriggerAtMillis(appointment: Appointments): Long {
        val appointmentDateTime = Calendar.getInstance().apply {
            time = appointment.date
            set(Calendar.HOUR_OF_DAY, appointment.time.hours)
            set(Calendar.MINUTE, appointment.time.minutes)
        }

        // Subtract the user's preferred notification time
        appointmentDateTime.add(Calendar.MINUTE, -(appointment.notificationPreferences?.minutesBefore ?: 0))
        appointmentDateTime.add(Calendar.HOUR_OF_DAY, -(appointment.notificationPreferences?.hoursBefore ?: 0))
        appointmentDateTime.add(Calendar.DAY_OF_MONTH, -(appointment.notificationPreferences?.daysBefore ?: 0))

        return appointmentDateTime.timeInMillis
    }
}