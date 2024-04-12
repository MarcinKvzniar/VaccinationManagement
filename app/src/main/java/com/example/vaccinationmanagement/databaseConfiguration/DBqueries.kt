package com.example.vaccinationmanagement.databaseConfiguration

import com.example.vaccinationmanagement.appointments.Appointments
import com.example.vaccinationmanagement.appointments.AppointmentsDAO
import java.sql.Connection
import java.sql.ResultSet

class DBqueries(private val connection: Connection) : AppointmentsDAO {

    // Retrieves an appointment by vaccineName from the database
    override fun getAppointmentByVaccine(vaccineName: String): Appointments? {
        val query = "{CALL getAppointment(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(7, vaccineName)
        val resultSet = callableStatement.executeQuery()

        return if (resultSet.next()) {
            mapResultSetToAppointment(resultSet)
        } else {
            null
        }
    }

    // Retrieves all appointments from the database
    override fun getAllAppointments(): Set<Appointments?>? {
        val query = "{CALL getAppointments()}"
        val callableStatement = connection.prepareCall(query)
        val resultSet = callableStatement.executeQuery()
        val skiers = mutableSetOf<Appointments?>()
        while (resultSet.next()) {
            skiers.add(mapResultSetToAppointment(resultSet))
        }
        return if (skiers.isEmpty()) null else skiers
    }

    // Inserts a new appointment into the database
    override fun insertAppointment(appointment: Appointments): Boolean {
        // Prepare the call to the MySQL stored procedure
        val call = "{CALL insertAppointment(?, ?, ?, ?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setLong(1, appointment.id)
        statement.setString(2, appointment.pesel)
        statement.setLong(3, appointment.doctorId)
        statement.setDate(4, appointment.date)
        statement.setTime(5, appointment.time)
        statement.setString(6, appointment.address)
        statement.setString(7, appointment.vaccineName)
        statement.setInt(8, appointment.dose)
        val result = !statement.execute()
        statement.close()
        return result
    }

    // Updates an existing appointment in the database
    override fun updateAppointment(vaccineName: String, appointment: Appointments): Boolean {
        val query = "{CALL updateSkier(?, ?, ?, ?, ?, ?, ?, ?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setLong(1, appointment.id)
        callableStatement.setString(2, appointment.pesel)
        callableStatement.setLong(3, appointment.doctorId)
        callableStatement.setDate(4, appointment.date)
        callableStatement.setTime(5, appointment.time)
        callableStatement.setString(6, appointment.address)
        callableStatement.setString(7, appointment.vaccineName)
        callableStatement.setInt(8, appointment.dose)

        return callableStatement.executeUpdate() > 0
    }

    // Deletes an appointment from the database
    override fun deleteAppointment(vaccineName: String): Boolean {
        val query = "{CALL deleteAppointment(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(7, vaccineName)
        return callableStatement.executeUpdate() > 0
    }


    // Maps a ResultSet row to an Appointment object
    private fun mapResultSetToAppointment(resultSet: ResultSet): Appointments? {
        return Appointments(
            id = resultSet.getLong("id"),
            pesel = resultSet.getString("pesel"),
            doctorId = resultSet.getLong("doctorId"),
            date = resultSet.getDate("date"),
            time = resultSet.getTime("time"),
            address = resultSet.getString("address"),
            vaccineName = resultSet.getString("vaccineName"),
            dose = resultSet.getInt("dose")
        )
    }
}