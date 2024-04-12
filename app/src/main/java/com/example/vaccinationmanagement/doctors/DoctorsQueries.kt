package com.example.vaccinationmanagement.doctors

import java.sql.Connection
import java.sql.ResultSet

class DoctorsQueries(private val connection: Connection) : DoctorsDAO {
    override fun getDoctorById(id: Int): Doctors? {
        val query = "{CALL getDoctor(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, id)
        val resultSet = callableStatement.executeQuery()

        return if (resultSet.next()) {
            mapResultSetToDoctor(resultSet)
        } else {
            null
        }
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
        val call = "{CALL insertDoctor(?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setInt(1, doctor.id)
        statement.setString(2, doctor.name)
        statement.setString(3, doctor.surname)
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun updateDoctor(id: Int, doctor: Doctors): Boolean {
        val query = "{CALL updateDoctor(?, ?, ?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, doctor.id)
        callableStatement.setString(2, doctor.name)
        callableStatement.setString(3, doctor.surname)

        return callableStatement.executeUpdate() > 0
    }

    override fun deleteDoctor(id: Int): Boolean {
        val query = "{CALL deleteDoctor(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, id)

        return callableStatement.executeUpdate() > 0
    }

    private fun mapResultSetToDoctor(resultSet: ResultSet): Doctors {
        return Doctors(
            id = resultSet.getInt("id"),
            name = resultSet.getString("name"),
            surname = resultSet.getString("surname")
        )
    }
}