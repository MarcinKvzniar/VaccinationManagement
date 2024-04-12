package com.example.vaccinationmanagement.doctors

interface DoctorsDAO {
    fun getDoctorById(id: Int): Doctors?

    fun getAllDoctors(): Set<Doctors?>?

    fun insertDoctor(doctor: Doctors) : Boolean

    fun updateDoctor(id: Int, doctor: Doctors) : Boolean

    fun deleteDoctor(id: Int) : Boolean
}