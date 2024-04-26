package com.example.vaccinationmanagement.activities.history

import java.sql.Date
import java.sql.Time

data class VaccinationDetail(
    val vaccineId: Int,
    val pesel: String,
    val doctorName: String,
    val doctorSurname: String,
    val date: Date,
    val time: Time,
    val address: String,
    val dose: Int,
    val vaccineName: String
)