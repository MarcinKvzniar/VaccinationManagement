package com.example.vaccinationmanagement.vaccines

import java.sql.Connection
import java.sql.ResultSet

class VaccinesQueries(private val connection : Connection) : VaccinesDAO {
    override fun getVaccineById(id: Int): Vaccines? {
        val query = "{CALL getVaccine(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, id)
        val resultSet = callableStatement.executeQuery()

        return if (resultSet.next()) {
            mapResultSetToVaccine(resultSet)
        } else {
            null
        }
    }

    override fun getVaccineByName(vaccineName: String): Vaccines? {
        val query = "{CALL getVaccineByName(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(1, vaccineName)
        val resultSet = callableStatement.executeQuery()

        return if (resultSet.next()) {
            mapResultSetToVaccine(resultSet)
        } else {
            null
        }
    }

    override fun getDosesByVaccineName(vaccineName: String): Int {
        val query = "{CALL getDosesByVaccineName(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(1, vaccineName)
        val resultSet = callableStatement.executeQuery()

        return if (resultSet.next()) {
            resultSet.getInt("requiredDoses")
        } else {
            0
        }
    }

    override fun getVaccineIdByVaccineName(vaccineName: String): Int {
        val preparedStatement = connection
            .prepareStatement("SELECT id FROM Vaccines WHERE vaccine_name = ?")
        preparedStatement.setString(1, vaccineName)

        val resultSet = preparedStatement.executeQuery()
        return if (resultSet.next()) {
            resultSet.getInt(1)
        } else {
            0
        }
    }

    override fun getAppointmentsCountForVaccine(vaccineName: String): Int {
        val preparedStatement = connection
            .prepareStatement("SELECT COUNT(*) FROM Appointments WHERE vaccine_name = ?")
        preparedStatement.setString(1, vaccineName)

        val resultSet = preparedStatement.executeQuery()
        return if (resultSet.next()) {
            resultSet.getInt(1)
        } else {
            0
        }
    }

    override fun getAllVaccines(): Set<Vaccines?>? {
        val query = "{CALL getVaccines()}"
        val callableStatement = connection.prepareCall(query)
        val resultSet = callableStatement.executeQuery()
        val vaccines = mutableSetOf<Vaccines?>()
        while (resultSet.next()) {
            vaccines.add(mapResultSetToVaccine(resultSet))
        }
        return if (vaccines.isEmpty()) null else vaccines
    }

    override fun insertVaccine(vaccine: Vaccines): Boolean {
        val call = "{CALL insertVaccine(?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        val id = vaccine.id
        if (id != null) {
            statement.setInt(1, id)
        } else {
            statement.setNull(1, java.sql.Types.INTEGER)
        }
        statement.setString(2, vaccine.vaccineName)
        statement.setInt(3, vaccine.requiredDoses)
        statement.setInt(4, vaccine.daysBetweenDoses)
        statement.setBoolean(5, vaccine.isRequired)
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun updateVaccine(id: Int, vaccine: Vaccines): Boolean {
        val query = "{CALL updateVaccine(?, ?, ?, ?, ?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, id)
        callableStatement.setString(2, vaccine.vaccineName)
        callableStatement.setInt(3, vaccine.requiredDoses)
        callableStatement.setInt(4, vaccine.daysBetweenDoses)
        callableStatement.setBoolean(5, vaccine.isRequired)

        return callableStatement.executeUpdate() > 0
    }

    override fun deleteVaccine(id: Int): Boolean {
        val query = "{CALL deleteVaccine(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setInt(1, id)

        return callableStatement.executeUpdate() > 0
    }

    private fun mapResultSetToVaccine(resultSet: ResultSet): Vaccines {
        return Vaccines(
            id = resultSet.getInt("id"),
            vaccineName = resultSet.getString("vaccineName"),
            requiredDoses = resultSet.getInt("requiredDoses"),
            daysBetweenDoses = resultSet.getInt("daysBetweenDoses"),
            isRequired = resultSet.getBoolean("isRequired")
        )
    }
}