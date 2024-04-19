package com.example.vaccinationmanagement.appointments

import java.sql.Connection
import java.sql.ResultSet

class AppointmentsQueries(private val connection: Connection) : AppointmentsDAO {

    // Retrieves an appointment by id from the database
    override fun getAppointmentById(id: Int): Appointments? {
        val query = "{CALL getAppointment(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, id)
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
        val appointments = mutableSetOf<Appointments?>()
        while (resultSet.next()) {
            appointments.add(mapResultSetToAppointment(resultSet))
        }
        return if (appointments.isEmpty()) null else appointments
    }

    // Inserts a new appointment into the database
    override fun insertAppointment(appointment: Appointments): Boolean {
        // Prepare the call to the MySQL stored procedure
        val call = "{CALL insertAppointment(?, ?, ?, ?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setInt(1, appointment.id)
        statement.setInt(2, appointment.vaccineId)
        statement.setString(3, appointment.pesel)
        statement.setInt(4, appointment.doctorId)
        statement.setDate(5, appointment.date)
        statement.setTime(6, appointment.time)
        statement.setString(7, appointment.address)
        statement.setInt(8, appointment.dose)
        val result = !statement.execute()
        statement.close()

        return result
    }

    // Updates an existing appointment in the database
    override fun updateAppointment(id: Int, appointment: Appointments): Boolean {
        val query = "{CALL updateAppointment(?, ?, ?, ?, ?, ?, ?, ?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, appointment.id)
        callableStatement.setInt(2, appointment.vaccineId)
        callableStatement.setString(3, appointment.pesel)
        callableStatement.setInt(4, appointment.doctorId)
        callableStatement.setDate(5, appointment.date)
        callableStatement.setTime(6, appointment.time)
        callableStatement.setString(7, appointment.address)
        callableStatement.setInt(8, appointment.dose)

        return callableStatement.executeUpdate() > 0
    }

    // Deletes an appointment from the database
    override fun deleteAppointment(id: Int): Boolean {
        val query = "{CALL deleteAppointment(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, id)

        return callableStatement.executeUpdate() > 0
    }


    // Maps a ResultSet row to an Appointment object
    private fun mapResultSetToAppointment(resultSet: ResultSet): Appointments {
        return Appointments(
            id = resultSet.getInt("id"),
            vaccineId = resultSet.getInt("vaccine_id"),
            pesel = resultSet.getString("pesel"),
            doctorId = resultSet.getInt("doctor_id"),
            date = resultSet.getDate("date"),
            time = resultSet.getTime("time"),
            address = resultSet.getString("address"),
            dose = resultSet.getInt("dose")
        )
    }
}