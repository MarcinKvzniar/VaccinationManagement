package com.example.vaccinationmanagement.dbConfig


import com.example.vaccinationmanagement.appointments.Appointments
import com.example.vaccinationmanagement.appointments.AppointmentsQueries
import com.example.vaccinationmanagement.doctors.Doctors
import com.example.vaccinationmanagement.doctors.DoctorsQueries
import com.example.vaccinationmanagement.patients.Patients
import com.example.vaccinationmanagement.patients.PatientsQueries
import java.sql.Date
import java.sql.Time

fun main() {
    try {
        // Getting connection using DBConnection class
        val connection = DBconnection.getConnection() // Getting a database connection using the DBConnection class
        val appointmentQuery = AppointmentsQueries(connection)
        val patientQuery = PatientsQueries(connection)
        val doctorQuery = DoctorsQueries(connection)

        // test insert a patient
        println("Testing insertPatient():") // Testing the insertPatient() method:
        val newPatient = Patients("02949285472", "John", "Doe", Date.valueOf("1995-10-10"))
        println("Insertion successful: ${patientQuery.insertPatient(newPatient)}")

        println("Testing getAllPatients():")
        println(patientQuery.getAllPatients())

        println("Testing insertDoctor():") // Testing the insertDoctor() method:
        val newDoctor = Doctors(1, "Dr. John Doe", "General Practitioner")
        println("Insertion successful: ${doctorQuery.insertDoctor(newDoctor)}")

        println("Testing getAllDoctors():")
        println(patientQuery.getAllDoctors())


        // Testing methods
        println("Testing insertAppointment():") // Testing the insertAppointment() method:
        val newAppointment = Appointments(1, "11111111111", 1, Date.valueOf("2024-04-12"), Time.valueOf("10:00:00"), "123 Main St", "Pfizer", 1)
        println("Insertion successful: ${appointmentQuery.insertAppointment(newAppointment)}")

        println("Testing getAllAppointments():")
        println(appointmentQuery.getAllAppointments())

//        println("Testing insertAppointment():") // Testing the insertAppointment() method:

//        // Creating a new Appointments object and printing the success or failure of inserting a new record
//        val newAppointment2 = Appointments(2, "8428928984294", 2, Date.valueOf("2021-10-10"), Time.valueOf("10:00:00"), "123 Main St", "Moderna", 1)
//        println("Insertion successful:${dbQueries.insertAppointment(newAppointment2)}")
//
//        println("Testing updateAppointment():")
//
//        // Creating an updated Appointments object, calling the updateAppointment method, and printing the success or failure
//        val updatedAppointments = Appointments(1, "8742874827822", 1, Date.valueOf("2021-10-10"), Time.valueOf("10:00:00"), "123 Main St", "Pfizer", 2)
//        println("Update successful: ${dbQueries.updateAppointment("Pfizer", updatedAppointments)}")
//
//        println("Testing deleteSkier():") // Testing the deleteAppointment() method:
//        println("Deletion successful:${dbQueries.deleteAppointment("Pfizer")}") // Calling the deleteAppointment method and printing the success or failure

        // Closing connection
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace() // Printing error information in case an exception occurs
    }
}
