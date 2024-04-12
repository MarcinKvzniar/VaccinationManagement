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
        val connection = DBconnection.getConnection()
        val appointmentQuery = AppointmentsQueries(connection)
        val patientQuery = PatientsQueries(connection)
        val doctorQuery = DoctorsQueries(connection)

        // test insert a patient
        println("Testing insertPatient():")
        val newPatient = Patients("12345678901", "Daniel", "Dave", Date.valueOf("2001-11-02"))
        println("Insertion successful: ${patientQuery.insertPatient(newPatient)}")

        println("Testing getAllPatients():")
        println(patientQuery.getAllPatients())

        println("Testing insertDoctor():")
        val newDoctor = Doctors(2, "Michael", "Jordan")
        println("Insertion successful: ${doctorQuery.insertDoctor(newDoctor)}")

        println("Testing getAllDoctors():")
        println(doctorQuery.getAllDoctors())


        // Testing methods
        println("Testing insertAppointment():")
        val newAppointment = Appointments(2, "12345678901", 2, Date.valueOf("2024-04-12"), Time.valueOf("12:30:00"), "Street Street", "SomeVaccine", 1)
        println("Insertion successful: ${appointmentQuery.insertAppointment(newAppointment)}")

        println("Testing getAllAppointments():")
        println(appointmentQuery.getAllAppointments())


        println("Testing updateAppointment():")
        val updatedAppointments = Appointments(1, "02949285472", 1, Date.valueOf("2021-10-10"), Time.valueOf("08:45:00"), "321 Middle St", "Pfizer", 2)
        println("Update successful: ${appointmentQuery.updateAppointment(1, updatedAppointments)}")

        println("Testing deleteSkier():")
        println("Deletion successful:${appointmentQuery.deleteAppointment(1)}")

        // Closing connection
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace() // Printing error information in case an exception occurs
    }
}
