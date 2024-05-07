package com.example.vaccinationmanagement.notifications

import java.sql.Connection

class NotificationQueries(private val connection: Connection) {

    // Function to insert a notification into the database
    fun insertNotification(notification: com.example.vaccinationmanagement.activities.notifications.Notification): Boolean {
        return try {
            val sql = "INSERT INTO notifications (vaccine_id, uid, notification_date, notification_time) VALUES (?, ?, ?, ?)"
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, notification.vaccineId)
            preparedStatement.setString(2, notification.uid)
            preparedStatement.setDate(3, notification.notificationDate)
            preparedStatement.setTime(4, notification.notificationTime)
            val rowsAffected = preparedStatement.executeUpdate()
            preparedStatement.close()
            rowsAffected > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
