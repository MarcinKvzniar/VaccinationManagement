package com.example.vaccinationmanagement.appointments

import java.sql.Time
import java.sql.Date

data class Appointments(
    var id: Long,
    var pesel: String,
    var doctorId: Long,
    var date: Date,
    var time: Time,
    var address: String,
    var vaccineName: String,
    var dose: Int,
)


