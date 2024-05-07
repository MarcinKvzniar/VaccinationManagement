package com.example.vaccinationmanagement.activities.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.vaccinationmanagement.appointments.Appointments
import com.example.vaccinationmanagement.appointments.AppointmentsDAO
import java.util.*
import java.util.concurrent.TimeUnit

/*
This class is responsible for scheduling these reminders, it iterates over all appointments and
schedules a reminder for each appointment that meets certain criteria (as determined by
the shouldScheduleReminder function).
 */


class ReminderService(private val context: Context, private val appointmentsDAO: AppointmentsDAO) {

    fun scheduleReminders() {
        val appointments = appointmentsDAO.getAllAppointments()
        appointments?.forEach { appointment ->
            if (appointment != null && shouldScheduleReminder(appointment)) {
                scheduleReminder(appointment)
            }
        }
    }

    private fun shouldScheduleReminder(appointment: Appointments): Boolean {
        val currentDateTime = Calendar.getInstance()
        val appointmentDateTime = Calendar.getInstance().apply {
            time = appointment.date
            set(Calendar.HOUR_OF_DAY, appointment.time.hours)
            set(Calendar.MINUTE, appointment.time.minutes)
        }

        val diff = appointmentDateTime.timeInMillis - currentDateTime.timeInMillis
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diff)

        return diffInHours in 0..24
    }

    private fun scheduleReminder(appointment: Appointments) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("appointment_id", appointment.id)
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAtMillisList =
            getTriggerAtMillis(appointment) // calculate the times at which the reminders should be triggered

        for ((index, triggerAtMillis) in triggerAtMillisList.withIndex()) {
            val pendingIntent = PendingIntent.getBroadcast(  // each pendingIntent unique because in other way it will overwrite each other
                context,
                appointment.id!! + index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun getTriggerAtMillis(appointment: Appointments): List<Long> {
        val appointmentDateTime = Calendar.getInstance().apply {
            time = appointment.date
            set(Calendar.HOUR_OF_DAY, appointment.time.hours)
            set(Calendar.MINUTE, appointment.time.minutes)
        }

        val reminderTimes = mutableListOf<Long>()

        // 24 hours before
        val twentyFourHoursBefore = appointmentDateTime.clone() as Calendar
        twentyFourHoursBefore.add(Calendar.HOUR_OF_DAY, -24)
        reminderTimes.add(twentyFourHoursBefore.timeInMillis)

        // 1 hour before
        val oneHourBefore = appointmentDateTime.clone() as Calendar
        oneHourBefore.add(Calendar.HOUR_OF_DAY, -1)
        reminderTimes.add(oneHourBefore.timeInMillis)

        return reminderTimes
    }
}