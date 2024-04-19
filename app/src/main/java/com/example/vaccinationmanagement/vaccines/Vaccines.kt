package com.example.vaccinationmanagement.vaccines

data class Vaccines (
    var id: Int,
    var vaccineName: String,
    var requiredDoses: Int,
    var daysBetweenDoses: Int,
    var isRequired: Boolean
)
