package com.example.vaccinationmanagement.patients

import java.time.LocalDate

data class Patients (
    var pesel: String,
    var name: String,
    var surname: String,
    var dateOfBirth: LocalDate,
)
