package com.example.vaccinationmanagement.activities.notifications

data class NotificationPreferences(
    val minutesBefore: Int,
    val hoursBefore: Int,
    val daysBefore: Int
)