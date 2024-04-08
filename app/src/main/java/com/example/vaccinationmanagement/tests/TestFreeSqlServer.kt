package com.example.vaccinationmanagement.tests


import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

object TestFreeSqlServer {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            // Loading the driver class requires a ClassNotFoundException
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        // Connection details
        val urlSB = StringBuilder("jdbc:mysql://")
        urlSB.append("sql11.freesqldatabase.com:3306/")
        urlSB.append("sql11691372") // database name
        urlSB.append("useUnicode=true&characterEncoding=utf-8")
        urlSB.append("sql11691372") // your user name
        urlSB.append("&password=NCgm9iT6hR") // generate password
        urlSB.append("&serverTimezone=CET")
        val connectionUrl = urlSB.toString()

        try {
            val conn = DriverManager.getConnection(connectionUrl)
            // Adding a new record
            addNewRecord(conn,
                "John Deere",
                "USA",
                "1990-01-01",
                "Rossignol",
                "Rossignol")

            // Deleting a record
//            deleteRecord(conn, "John Deere")

            // Retrieving data
            retrieveData(conn, "USA")

            // Updating data
            updateData(conn, "John Doe", "Poland")

            // Checking and adding a new column
            checkAndAddColumn(conn, "alpine_skiers","personal record2")

            // Displaying tables in the database
            displayTables(conn)

            conn.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun addNewRecord(conn: Connection,
                             name: String,
                             country: String,
                             date_of_birth: String,
                             skis: String,
                             ski_boots: String) {
        try {
            val insertStatement = conn.prepareStatement(
                "INSERT INTO `alpine_skiers`(`name`, `country`, `date_of_birth`, `skis`, `ski_boots`) "
                        + "VALUES (?,?,?,?,?)")
            insertStatement.setString(1, name)
            insertStatement.setString(2, country)
            insertStatement.setDate(3, java.sql.Date.valueOf(date_of_birth))
            insertStatement.setString(4, skis)
            insertStatement.setString(5, ski_boots)
            val rowsAffected = insertStatement.executeUpdate()
            if (rowsAffected > 0) {
                println("New record has been added successfully.")
            } else {
                println("Failed to add a new record.")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun deleteRecord(conn: Connection, name:String) {
        try {
            val deleteStatement = conn.prepareStatement("DELETE FROM `alpine_skiers` WHERE `name` = ?")
            deleteStatement.setString(1, name)
            val rowsAffected = deleteStatement.executeUpdate()
            if (rowsAffected > 0) {
                println("Record has been deleted successfully.")
            } else {
                println("No record found to delete.")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun retrieveData(conn: Connection, selectedCountry:
    String) {
        try {
            val selectQuery = "SELECT * FROM alpine_skiers WHERE country = ?"
            val selectStatement = conn.prepareStatement(selectQuery)
            selectStatement.setString(1, selectedCountry)
            val resultSet = selectStatement.executeQuery()
            println("Skiers from $selectedCountry:")
            printResultSet(resultSet)
            resultSet.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun updateData(conn: Connection, selectedName: String, updateCountry: String) {
        try {
            val updateStatement = conn.prepareStatement("UPDATE `alpine_skiers` SET `country` = ? WHERE `name` = ?")
            updateStatement.setString(1, updateCountry)
            updateStatement.setString(2, selectedName)
            val rowsAffected = updateStatement.executeUpdate()
            if (rowsAffected > 0) {
                println("Skier's data has been updated successfully.")
            } else {
                println("Failed to update skier's data.")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun checkAndAddColumn(conn: Connection, table:
    String ,newColumn: String) {
        try {
            val metaData = conn.metaData
            val resultSet = metaData.getColumns(null, null,
                table, newColumn)
            if (resultSet.next()) {
                println("Column $newColumn already exists in table $table.")
            } else {
                val alterTableQuery = "ALTER TABLE $table ADD COLUMN $newColumn DOUBLE"
                val statement = conn.createStatement()
                statement.executeUpdate(alterTableQuery)
                println("New column $newColumn has been successfully added to table $table.")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun displayTables(conn: Connection) {
        try {
            println("Tables:")
            val showTablesST = conn.prepareStatement("SHOW TABLES")
            val rs1 = showTablesST.executeQuery()
            while (rs1.next()) {
                val s = rs1.getString(1)
                print("$s ")
            }
            println("")
            println("*************** skiers ***************")
            val selectAllSt = conn.prepareStatement("SELECT * FROM alpine_skiers;")
            val rsAllSt = selectAllSt.executeQuery()
            printResultSet(rsAllSt)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    @Throws(SQLException::class)
    private fun printResultSet(resultSet: ResultSet) {
        val rsmd = resultSet.metaData
        val columnsNumber = rsmd.columnCount
        while (resultSet.next()) {
            for (i in 1..columnsNumber) {
                if (i > 1) print(", ")
                val columnValue = resultSet.getString(i)
                print(rsmd.getColumnName(i) + ": " +
                        columnValue)
            }
            println("")
        }
        println("")
    }
}