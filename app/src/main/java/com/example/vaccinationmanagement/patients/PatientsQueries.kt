package com.example.vaccinationmanagement.patients

import java.sql.Connection
import java.sql.ResultSet

class PatientsQueries(private val connection : Connection) : PatientsDAO {
    override fun getPatientByPesel(pesel: String): Patients? {
        val query = "{CALL getPatient(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(1, pesel)
        val resultSet = callableStatement.executeQuery()

        return if (resultSet.next()) {
            mapResultSetToPatient(resultSet)
        } else {
            null
        }
    }

    override fun getPeselByUID(uid: String?): String? {
        val preparedStatement = connection
            .prepareStatement("SELECT pesel FROM Patients WHERE u_id = ?")
        preparedStatement.setString(1, uid)

        val resultSet = preparedStatement.executeQuery()
        return if (resultSet.next()) {
            resultSet.getString(1)
        } else {
            null
        }
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
        val call = "{CALL insertPatient(?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, patient.pesel)
        statement.setString(2, patient.uId)
        statement.setString(3, patient.name)
        statement.setString(4, patient.surname)
        statement.setDate(5, patient.dateOfBirth)
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun updatePatient(pesel: String, patient: Patients): Boolean {
        val query = "{CALL updatePatient(?, ?, ?, ?, ?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(1, patient.pesel)
        callableStatement.setString(2, patient.uId)
        callableStatement.setString(3, patient.name)
        callableStatement.setString(4, patient.surname)
        callableStatement.setDate(5, patient.dateOfBirth)

        return callableStatement.executeUpdate() > 0
    }

    override fun deletePatient(pesel: String): Boolean {
        val query = "{CALL deletePatient(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(1, pesel)

        return callableStatement.executeUpdate() > 0
    }

    private fun mapResultSetToPatient(resultSet: ResultSet): Patients {
        return Patients(
            pesel = resultSet.getString("pesel"),
            uId = resultSet.getString("u_id"),
            name = resultSet.getString("name"),
            surname = resultSet.getString("surname"),
            dateOfBirth = resultSet.getDate("date_of_birth")
        )
    }
}