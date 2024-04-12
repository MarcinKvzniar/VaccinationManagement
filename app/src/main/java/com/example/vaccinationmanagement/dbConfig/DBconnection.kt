package com.example.vaccinationmanagement.dbConfig

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

// Singleton object responsible for managing database connections
object DBconnection {
    // Database connection details
    private const val URL = "jdbc:mysql://sql11.freesqldatabase.com:3306/sql11698482?useUnicode=true&characterEncoding=utf-8&serverTimezone=CET"   // TODO set in environmental variables
    private const val USER = "sql11698482" // TODO set in environmental variables
    private const val PASS = "MT4yfZGyM6" // TODO set in environmental variables

    // Static initializer block to register the MySQL JDBC driver
    init {
        Class.forName("com.mysql.cj.jdbc.Driver")
    }

    // Function to get a connection to the database
    fun getConnection(): Connection {
        try {
            return DriverManager.getConnection(URL, USER, PASS)
        } catch (ex: SQLException) {
            throw RuntimeException("Error connecting to the database", ex)
        }
    }

    // Main function to test the database connection
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            // Getting a connection
            val conn = getConnection()
            // Closing the connection
            conn.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}




