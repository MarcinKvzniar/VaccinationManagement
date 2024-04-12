package com.example.vaccinationmanagement.patients

import com.example.vaccinationmanagement.appointments.Appointments
import java.sql.Connection
import java.sql.ResultSet

class PatientsQueries(private val connection : Connection) : PatientsDAO {
    override fun getPatientByPesel(pesel: String): Patients? {
        TODO("Not yet implemented")
    }

    override fun getAllPatients(): Set<Patients?>? {
        val query = "{CALL getPatients()}"
        val callableStatement = connection.prepareCall(query)
        val resultSet = callableStatement.executeQuery()
        val patients = mutableSetOf<Patients?>()
        while (resultSet.next()) {
            patients.add(mapResultSetToPatient(resultSet))
        }
        return if (patients.isEmpty()) null else patients
    }

    override fun insertPatient(patient: Patients): Boolean {
        val call = "{CALL insertPatient(?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, patient.pesel)
        statement.setString(2, patient.name)
        statement.setString(3, patient.surname)
        statement.setDate(4, patient.dateOfBirth)
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun updatePatient(pesel: String, patient: Patients): Boolean {
        TODO("Not yet implemented")
    }

    override fun deletePatient(pesel: String): Boolean {
        TODO("Not yet implemented")
    }

    private fun mapResultSetToPatient(resultSet: ResultSet): Patients? {
        return Patients(
            pesel = resultSet.getString("pesel"),
            name = resultSet.getString("name"),
            surname = resultSet.getString("surname"),
            dateOfBirth = resultSet.getDate("date_of_birth")
        )
    }
}