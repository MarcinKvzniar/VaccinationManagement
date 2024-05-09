package com.example.vaccinationmanagement.activities.notifications

import java.sql.Date
import java.sql.Time

data class Notification(
    val notificationId: Int? = null,
    val vaccineId: Int,
    val pesel: String,
    val notificationDate: Date,
    val notificationTime: Time
)
