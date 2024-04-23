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
            .prepareStatement("SELECT pesel FROM Patients WHERE uid = ?")
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

    /*
    Try this function instead (do not forget to add imports):
    fun insertPatient(patient: Patients)  {
    val sql = "INSERT INTO Patients (pesel, name, surname, dateOfBirth) VALUES (?, ?, ?, ?)"

    try {
        val connection = DBconnection.getConnection() // Get the database connection
        val preparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, patient.pesel)
        preparedStatement.setString(2, patient.name)
        preparedStatement.setString(3, patient.surname)
        preparedStatement.setDate(4, patient.dateOfBirth)

        Log.d("SQL Query", "Executing query: $sql with parameters: ${patient.pesel}, ${patient.name}, ${patient.surname}, ${patient.dateOfBirth}")

        preparedStatement.executeUpdate()
        connection.close() // Close the database connection
    } catch (e: SQLException) {
        Log.e("SQL Error", "Error executing query: $sql", e)
    }
}
     */

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
            uId = resultSet.getString("uid"),
            name = resultSet.getString("name"),
            surname = resultSet.getString("surname"),
            dateOfBirth = resultSet.getDate("date_of_birth")
        )
    }
}