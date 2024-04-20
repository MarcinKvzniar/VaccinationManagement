package com.example.vaccinationmanagement.dbConfig

//import com.example.vaccinationmanagement.appointments.Appointments
//import com.example.vaccinationmanagement.appointments.AppointmentsQueries
//import com.example.vaccinationmanagement.doctors.Doctors
//import com.example.vaccinationmanagement.doctors.DoctorsQueries
//import com.example.vaccinationmanagement.patients.Patients
//import com.example.vaccinationmanagement.patients.PatientsQueries
//import com.example.vaccinationmanagement.vaccines.Vaccines
//import com.example.vaccinationmanagement.vaccines.VaccinesQueries
//import java.sql.Date
//import java.sql.Time
//
//fun main() {
//    try {
//        // Getting connection using DBConnection class
//        val connection = DBconnection.getConnection()
//        val appointmentQuery = AppointmentsQueries(connection)
//        val patientQuery = PatientsQueries(connection)
//        val doctorQuery = DoctorsQueries(connection)
//        val vaccineQuery = VaccinesQueries(connection)
//
//        // test insert a patient
//        println("Testing insertPatient():")
//        val newPatient = Patients("12345678901", "a24fg57ghy18ifur72ht62trk9ly", "Daniel", "Dave", Date.valueOf("2001-11-02"))
//        println("Insertion successful: ${patientQuery.insertPatient(newPatient)}")
//
//        println("Testing getAllPatients():")
//        println(patientQuery.getAllPatients())
//
//        // test insert a doctor
//        println("Testing insertDoctor():")
//        val newDoctor = Doctors(1, "Michael", "Jordan")
//        println("Insertion successful: ${doctorQuery.insertDoctor(newDoctor)}")
//
//        println("Testing getAllDoctors():")
//        println(doctorQuery.getAllDoctors())
//
//        // test insert a vaccine
//        println("Testing insertVaccine():")
//        val newVaccine = Vaccines(1, "Pfizer", 2, 14, false)
//        println("Insertion successful: ${vaccineQuery.insertVaccine(newVaccine)}")
//
//        println("Testing getAllVaccines():")
//        println(vaccineQuery.getAllVaccines())
//
//
//        // Testing methods
//        println("Testing insertAppointment():")
//        val newAppointment = Appointments(1, 1, "12345678901", 1, Date.valueOf("2024-04-12"), Time.valueOf("12:30:00"), "Street Street", 1)
//        println("Insertion successful: ${appointmentQuery.insertAppointment(newAppointment)}")
//
//        println("Testing getAllAppointments():")
//        println(appointmentQuery.getAllAppointments())
//
//        // Closing connection
//        connection.close()
//    } catch (e: Exception) {
//        e.printStackTrace() // Printing error information in case an exception occurs
//    }
//}
