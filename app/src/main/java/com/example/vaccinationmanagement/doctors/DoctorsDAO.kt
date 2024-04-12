package com.example.vaccinationmanagement.doctors

interface DoctorsDAO {
    fun getDoctorById(id: Long): Doctors?

    fun getAllDoctors(): Set<Doctors?>?

    fun insertDoctor(doctor: Doctors) : Boolean

    fun updateDoctor(id: Long, doctor: Doctors) : Boolean

    fun deleteDoctor(id: Long) : Boolean
}