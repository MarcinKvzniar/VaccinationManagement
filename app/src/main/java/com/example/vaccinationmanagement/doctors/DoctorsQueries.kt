package com.example.vaccinationmanagement.doctors

import java.sql.Connection
import java.sql.ResultSet

class DoctorsQueries(private val connection: Connection) : DoctorsDAO {
    override fun getDoctorById(id: Int): Doctors? {
        val preparedStatement = connection
            .prepareStatement("SELECT * FROM Doctors WHERE id = ?")
        preparedStatement.setInt(1, id)

        val resultSet = preparedStatement.executeQuery()
        return if (resultSet.next()) {
            mapResultSetToDoctor(resultSet)
        } else {
            null
        }
    }

    override fun getAllDoctors(): Set<Doctors?>? {
        val query = "{CALL getAllDoctors()}"
        val callableStatement = connection.prepareCall(query)
        val resultSet = callableStatement.executeQuery()
        val doctors = mutableSetOf<Doctors?>()
        while (resultSet.next()) {
            doctors.add(mapResultSetToDoctor(resultSet))
        }
        return if (doctors.isEmpty()) null else doctors
    }

    override fun insertDoctor(doctor: Doctors): Boolean {
        val preparedStatement = connection
            .prepareStatement("INSERT INTO Doctors (name, surname) VALUES (?, ?)")
        preparedStatement.setString(1, doctor.name)
        preparedStatement.setString(2, doctor.surname)

        val result = preparedStatement.executeUpdate() > 0
        preparedStatement.close()
        return result
    }

    override fun updateDoctor(id: Int, doctor: Doctors): Boolean {
        val preparedStatement = connection
            .prepareStatement("UPDATE Doctors SET name = ?, surname = ? WHERE id = ?")
        preparedStatement.setString(1, doctor.name)
        preparedStatement.setString(2, doctor.surname)
        preparedStatement.setInt(3, id)

        return preparedStatement.executeUpdate() > 0
    }

    override fun deleteDoctor(id: Int): Boolean {
        val preparedStatement = connection
            .prepareStatement("DELETE FROM Doctors WHERE id = ?")
        preparedStatement.setInt(1, id)

        return preparedStatement.executeUpdate() > 0
    }

    private fun mapResultSetToDoctor(resultSet: ResultSet): Doctors {
        return Doctors(
            id = resultSet.getInt("id"),
            name = resultSet.getString("name"),
            surname = resultSet.getString("surname")
        )
    }
}