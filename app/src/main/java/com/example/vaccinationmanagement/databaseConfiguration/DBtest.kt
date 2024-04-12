package com.example.vaccinationmanagement.databaseConfiguration


import com.example.vaccinationmanagement.appointments.Appointments
import java.sql.Date
import java.sql.Time

fun main() {
    try {
        // Getting connection using DBConnection class
        val connection = DBconnection.getConnection() // Getting a database connection using the DBConnection class
        val dbQueries = DBqueries(connection) // Creating a DBqueries object to perform database queries

        // Testing methods
        println("Testing insertAppointment():") // Testing the insertAppointment() method:

        // Creating a new Appointments object and printing the success or failure of inserting a new record
        val newAppointment = Appointments(1, "029492854728", 1, Date.valueOf("2021-10-10"), Time.valueOf("10:00:00"), "123 Main St", "Pfizer", 1)
        println("Insertion successful: ${dbQueries.insertAppointment(newAppointment)}")

        println("Testing getAllSkiers():")
        println(dbQueries.getAllAppointments())

        println("Testing insertSkier():") // Testing the insertAppointment() method:

        // Creating a new Appointments object and printing the success or failure of inserting a new record
        val newAppointment2 = Appointments(2, "8428928984294", 2, Date.valueOf("2021-10-10"), Time.valueOf("10:00:00"), "123 Main St", "Moderna", 1)
        println("Insertion successful:${dbQueries.insertAppointment(newAppointment2)}")

        println("Testing updateAppointment():")

        // Creating an updated Appointments object, calling the updateAppointment method, and printing the success or failure
        val updatedAppointments = Appointments(1, "8742874827822", 1, Date.valueOf("2021-10-10"), Time.valueOf("10:00:00"), "123 Main St", "Pfizer", 2)
        println("Update successful: ${dbQueries.updateAppointment("Pfizer", updatedAppointments)}")

        println("Testing deleteSkier():") // Testing the deleteAppointment() method:
        println("Deletion successful:${dbQueries.deleteAppointment("Pfizer")}") // Calling the deleteAppointment method and printing the success or failure

        // Closing connection
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace() // Printing error information in case an exception occurs
    }
}
