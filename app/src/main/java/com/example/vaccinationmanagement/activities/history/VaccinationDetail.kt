package com.example.vaccinationmanagement.activities.history

import java.sql.Date
import java.sql.Time

data class VaccinationDetail(
    val id: Int,
    val vaccineId: Int,
    val pesel: String,
    val doctorName: String,
    val doctorSurname: String,
    var date: Date,
    var time: Time,
    var address: String,
    val dose: Int,
    val vaccineName: String
)