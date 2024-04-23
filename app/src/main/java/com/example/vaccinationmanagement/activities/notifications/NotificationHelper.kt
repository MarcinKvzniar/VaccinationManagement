package com.example.vaccinationmanagement.activities.notifications

// NotificationHelper.kt
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.appointments.Appointments


//NotificationHelper is used to create the notification channel and build the notifications.
// The UserNotificationService class uses this helper to schedule the notifications and display them immediately.
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "VACCINATION_REMINDER_CHANNEL"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildNotification(title: String, content: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    @SuppressLint("MissingPermission")
    fun notify(id: Int, notification: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(context)) {
            notify(id, notification.build())
        }
    }

    fun scheduleNotification(appointment: Appointments) {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("appointmentId", appointment.id)
        val pendingIntent = PendingIntent.getBroadcast(context, appointment.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, appointment.date.time, pendingIntent)
    }

    fun cancelNotification(appointment: Appointments) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, appointment.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

}