package com.example.vaccinationmanagement.appointments

import com.example.vaccinationmanagement.appointments.Appointments

// Interface defining operations to access appointments data (DAO - Data Access Object)
interface AppointmentsDAO {
    fun getAppointmentByVaccine(vaccineName: String): Appointments?

    fun getAllAppointments(): Set<Appointments?>?

    fun insertAppointment(appointment: Appointments) : Boolean

    fun updateAppointment(vaccineName: String, appointment: Appointments) : Boolean

    fun deleteAppointment(vaccineName: String) : Boolean
}