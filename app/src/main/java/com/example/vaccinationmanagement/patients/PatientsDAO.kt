package com.example.vaccinationmanagement.patients

interface PatientsDAO {
    fun getPatientByPesel(pesel: String): Patients?

    fun getAllPatients(): Set<Patients?>?

    fun insertPatient(patient: Patients) : Boolean

    fun updatePatient(pesel: String, patient: Patients) : Boolean

    fun deletePatient(pesel: String) : Boolean
}