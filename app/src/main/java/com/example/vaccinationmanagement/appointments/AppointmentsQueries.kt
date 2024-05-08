package com.example.vaccinationmanagement.appointments

import android.util.Log
import java.sql.Connection
import java.sql.ResultSet

class AppointmentsQueries(private val connection: Connection) : AppointmentsDAO {

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

    override fun getAllAppointments(): Set<Appointments?>? {
        val query = "{CALL getAllAppointments()}"
        val callableStatement = connection.prepareCall(query)
        val resultSet = callableStatement.executeQuery()
        val appointments = mutableSetOf<Appointments?>()
        while (resultSet.next()) {
            appointments.add(mapResultSetToAppointment(resultSet))
        }
        return if (appointments.isEmpty()) null else appointments
    }

    override fun insertAppointment(appointment: Appointments): Boolean {
        val call = "{CALL insertAppointment(?, ?, ?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)

        statement.setInt(1, appointment.vaccineId)
        statement.setString(2, appointment.pesel)
        statement.setInt(3, appointment.doctorId)
        statement.setDate(4, appointment.date)
        statement.setTime(5, appointment.time)
        statement.setString(6, appointment.address)
        statement.setInt(7, appointment.dose)
        val result = !statement.execute()
        statement.close()

        return result
    }

    override fun updateAppointment(id: Int, appointment: Appointments): Boolean {
        val query = "{CALL updateAppointment(?, ?, ?, ?, ?, ?, ?, ?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, id)
        callableStatement.setInt(2, appointment.vaccineId ?: 0)
        callableStatement.setString(3, appointment.pesel)
        callableStatement.setInt(4, appointment.doctorId ?: 0)
        callableStatement.setDate(5, appointment.date)
        callableStatement.setTime(6, appointment.time)
        callableStatement.setString(7, appointment.address)
        callableStatement.setInt(8, appointment.dose ?: 0)

        val affectedRows = callableStatement.executeUpdate()
        val result = affectedRows > 0
        if (result) {
            Log.d("AppointmentsQueries", "Appointment with id: $id updated successfully. Rows affected: $affectedRows")
        } else {
            Log.d("AppointmentsQueries", "Failed to update appointment with id: $id. Rows affected: $affectedRows")
        }

        var warnings = callableStatement.warnings
        while (warnings != null) {
            Log.w("AppointmentsQueries", "SQL Warning: ${warnings.message}")
            warnings = warnings.nextWarning
        }

        callableStatement.close()

        return result
    }

    override fun deleteAppointment(id: Int): Boolean {
        val query = "{CALL deleteAppointment(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, id)

        return callableStatement.executeUpdate() > 0
    }

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