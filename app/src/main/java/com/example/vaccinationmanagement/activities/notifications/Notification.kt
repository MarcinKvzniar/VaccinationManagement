package com.example.vaccinationmanagement.activities.notifications

import java.sql.Date
import java.sql.Time

data class Notification(
    val vaccineId: Int,
    val uid: String,
    val notificationDate: Date,
    val notificationTime: Time
)
