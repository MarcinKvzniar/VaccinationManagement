
package com.example.vaccinationmanagement.activities.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.vaccinationmanagement.appointments.Appointments
import com.example.vaccinationmanagement.appointments.AppointmentsDAO
import java.util.*

class UserNotificationService(private val context: Context, private val appointmentsDAO: AppointmentsDAO) {

    val notificationHelper = NotificationHelper(context)


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

        appointmentDateTime.add(Calendar.MINUTE, -(appointment.notificationPreferences?.minutesBefore ?: 0))
        appointmentDateTime.add(Calendar.HOUR_OF_DAY, -(appointment.notificationPreferences?.hoursBefore ?: 0))
        appointmentDateTime.add(Calendar.DAY_OF_MONTH, -(appointment.notificationPreferences?.daysBefore ?: 0))

        return appointmentDateTime.timeInMillis
    }
}