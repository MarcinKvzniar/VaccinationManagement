package com.example.vaccinationmanagement.appointments

// Interface defining operations to access appointments data (DAO - Data Access Object)
interface AppointmentsDAO {
    fun getAppointmentById(id: Int): Appointments?

    fun getAllAppointments(): Set<Appointments?>?

    fun insertAppointment(appointment: Appointments) : Boolean

    fun updateAppointment(id: Int, appointment: Appointments) : Boolean

    fun deleteAppointment(id: Int) : Boolean
}