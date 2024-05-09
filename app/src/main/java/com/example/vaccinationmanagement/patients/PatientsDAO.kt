package com.example.vaccinationmanagement.patients

// Interface defining operations to access patients data (DAO - Data Access Object)
interface PatientsDAO {
    fun getPatientByPesel(pesel: String): Patients?

    fun getPeselByUID(uid: String?): String?

    fun getAllPatients(): Set<Patients?>?

    fun insertPatient(patient: Patients) : Boolean

    fun updatePatient(pesel: String, patient: Patients) : Boolean

    fun deletePatient(pesel: String) : Boolean
}