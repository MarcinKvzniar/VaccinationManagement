package com.example.vaccinationmanagement.appointments

import java.sql.Connection
import java.sql.ResultSet

class AppointmentsQueries(private val connection: Connection) : AppointmentsDAO {

    // Retrieves an appointment by vaccineName from the database
    override fun getAppointmentByVaccineName(vaccineName: String): Appointments? {
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
        statement.setString(2, appointment.pesel)
        statement.setInt(3, appointment.doctorId)
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
        callableStatement.setInt(1, appointment.id)
        callableStatement.setString(2, appointment.pesel)
        callableStatement.setInt(3, appointment.doctorId)
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
            id = resultSet.getInt("id"),
            pesel = resultSet.getString("pesel"),
            doctorId = resultSet.getInt("doctor_id"),
            date = resultSet.getDate("date"),
            time = resultSet.getTime("time"),
            address = resultSet.getString("address"),
            vaccineName = resultSet.getString("vaccination_name"),
            dose = resultSet.getInt("dose")
        )
    }
}