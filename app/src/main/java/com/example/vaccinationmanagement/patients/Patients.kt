package com.example.vaccinationmanagement.patients

import java.sql.Date

data class Patients (
    var pesel: String,
    var name: String,
    var surname: String,
    var dateOfBirth: Date,
)
