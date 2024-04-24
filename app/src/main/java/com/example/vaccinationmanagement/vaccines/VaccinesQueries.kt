package com.example.vaccinationmanagement.vaccines

import java.sql.Connection
import java.sql.ResultSet

class VaccinesQueries(private val connection : Connection) : VaccinesDAO {
    override fun getVaccineById(id: Int): Vaccines? {
        val preparedStatement = connection
            .prepareStatement("SELECT * FROM Vaccines WHERE id = ?")
        preparedStatement.setInt(1, id)

        val resultSet = preparedStatement.executeQuery()
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

    override fun doesVaccineExist(vaccineName: String): Boolean {
        val query = "{? = CALL doesVaccineExist(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.registerOutParameter(1, java.sql.Types.INTEGER)
        callableStatement.setString(2, vaccineName)
        callableStatement.execute()

        val exists = callableStatement.getInt(1) > 0
        callableStatement.close()
        return exists
    }

    override fun getVaccineIdByVaccineName(vaccineName: String): Int {
        val query = "{CALL getVaccineIdByVaccineName(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(1, vaccineName)
        val resultSet = callableStatement.executeQuery()

        return if (resultSet.next()) {
            resultSet.getInt(1)
        } else {
            0
        }
    }

    override fun getAppointmentsCountForVaccine(vaccineName: String): Int {
        val query = "{? = CALL getAppointmentsCountForVaccine(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.registerOutParameter(1, java.sql.Types.INTEGER)
        callableStatement.setString(2, vaccineName)
        callableStatement.execute()

        val totalAppointments = callableStatement.getInt(1)
        callableStatement.close()
        return totalAppointments
    }

    override fun getDosesByVaccineName(vaccineName: String): Int {
        val query = "{CALL getDosesByVaccineName(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(1, vaccineName)
        val resultSet = callableStatement.executeQuery()

        return if (resultSet.next()) {
            resultSet.getInt(1)
        } else {
            0
        }
    }

    override fun getAllVaccines(): Set<Vaccines?>? {
        val preparedStatement = connection
            .prepareStatement("SELECT * FROM Vaccines")

        val resultSet = preparedStatement.executeQuery()
        val vaccines = mutableSetOf<Vaccines?>()
        while (resultSet.next()) {
            vaccines.add(mapResultSetToVaccine(resultSet))
        }
        return if (vaccines.isEmpty()) {
            null
        } else {
            vaccines
        }
    }

    override fun insertVaccine(vaccine: Vaccines): Boolean {
        val preparedStatement = connection
            .prepareStatement("INSERT INTO Vaccines (vaccine_name, required_doses, days_between_doses, is_required) VALUES (?, ?, ?, ?)")
        preparedStatement.setString(1, vaccine.vaccineName)
        preparedStatement.setInt(2, vaccine.requiredDoses)
        preparedStatement.setInt(3, vaccine.daysBetweenDoses)
        preparedStatement.setBoolean(4, vaccine.isRequired)

        val result = preparedStatement.executeUpdate() > 0
        preparedStatement.close()
        return result
    }

    override fun updateVaccine(id: Int, vaccine: Vaccines): Boolean {
        val preparedStatement = connection
            .prepareStatement("UPDATE Vaccines SET vaccine_name = ?, required_doses = ?, days_between_doses = ?, is_required = ? WHERE id = ?")
        preparedStatement.setString(1, vaccine.vaccineName)
        preparedStatement.setInt(2, vaccine.requiredDoses)
        preparedStatement.setInt(3, vaccine.daysBetweenDoses)
        preparedStatement.setBoolean(4, vaccine.isRequired)
        preparedStatement.setInt(5, id)

        return preparedStatement.executeUpdate() > 0
    }

    override fun deleteVaccine(id: Int): Boolean {
        val preparedStatement = connection
            .prepareStatement("DELETE FROM Vaccines WHERE id = ?")
        preparedStatement.setInt(1, id)

        return preparedStatement.executeUpdate() > 0
    }

    private fun mapResultSetToVaccine(resultSet: ResultSet): Vaccines {
        return Vaccines(
            id = resultSet.getInt("id"),
            vaccineName = resultSet.getString("vaccine_name"),
            requiredDoses = resultSet.getInt("required_doses"),
            daysBetweenDoses = resultSet.getInt("days_between_doses"),
            isRequired = resultSet.getBoolean("is_required")
        )
    }
}