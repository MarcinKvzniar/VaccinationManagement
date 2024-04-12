package com.example.vaccinationmanagement.data

data class VaccinationData(
    var doctorId: Long,
    var date: String = "",
    var time: String = "",
    var address: String = "",
    var vaccineName: String = "",
    var dose: Int,
)


