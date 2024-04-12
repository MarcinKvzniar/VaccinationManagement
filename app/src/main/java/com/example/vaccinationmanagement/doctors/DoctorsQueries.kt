package com.example.vaccinationmanagement.doctors

import com.example.vaccinationmanagement.patients.Patients
import java.sql.Connection
import java.sql.ResultSet

class DoctorsQueries(private val connection: Connection) : DoctorsDAO {
    override fun getDoctorById(id: Int): Doctors? {
        TODO("Not yet implemented")
    }

    override fun getAllDoctors(): Set<Doctors?>? {
        val query = "{CALL getDoctors()}"
        val callableStatement = connection.prepareCall(query)
        val resultSet = callableStatement.executeQuery()
        val doctors = mutableSetOf<Doctors?>()
        while (resultSet.next()) {
            doctors.add(mapResultSetToDoctor(resultSet))
        }
        return if (doctors.isEmpty()) null else doctors
    }

    override fun insertDoctor(doctor: Doctors): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateDoctor(id: Int, doctor: Doctors): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteDoctor(id: Int): Boolean {
        TODO("Not yet implemented")
    }
}