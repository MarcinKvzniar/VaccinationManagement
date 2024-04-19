package com.example.vaccinationmanagement.vaccines

import com.example.vaccinationmanagement.patients.Patients

interface VaccinesDAO {
    fun getVaccineById(id: Int): Vaccines?

    fun getVaccineByName(vaccineName: String): Vaccines?

    fun getAllVaccines(): Set<Vaccines?>?

    fun insertVaccine(vaccine: Vaccines) : Boolean

    fun updateVaccine(id: Int, vaccine: Vaccines) : Boolean

    fun deleteVaccine(id: Int) : Boolean
}