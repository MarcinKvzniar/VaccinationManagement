package com.example.vaccinationmanagement.activities.history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.activities.HomeActivity
import com.example.vaccinationmanagement.appointments.Appointments
import com.example.vaccinationmanagement.appointments.AppointmentsQueries
import com.example.vaccinationmanagement.dbConfig.DBconnection
import com.example.vaccinationmanagement.doctors.DoctorsQueries
import com.example.vaccinationmanagement.patients.PatientsQueries
import com.example.vaccinationmanagement.vaccines.VaccinesQueries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

/**
 * HistoryActivity is an activity class that handles the display
 * and management of vaccination history.
 * It extends AppCompatActivity, which is a base class for activities
 * that use the support library action bar features.
 */
class HistoryActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBackHome: Button
    private var vaccinationList: MutableList<VaccinationDetail> = mutableListOf()

    /**
     * This is the first callback and called when this activity is first created.
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Initialize views and set click listeners
        recyclerView = findViewById(R.id.historyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = HistoryAdapter(vaccinationList)
        recyclerView.adapter = historyAdapter

        btnBackHome = findViewById(R.id.btnBackButton)
        btnBackHome.setOnClickListener {
            startActivity(
                Intent(
                    this@HistoryActivity,
                    HomeActivity::class.java
                )
            )
        }

        // Fetch vaccination data from the database
        fetchVaccinationData()
    }

    /**
     * Fetch vaccination data from the database
     * @param currentUserPesel The PESEL number of the current user
     * @return A list of VaccinationDetail objects representing the
     * vaccination history of the current user
     */
    private suspend fun fetchVaccinationDataFromDB(currentUserPesel: String): MutableList<VaccinationDetail> {
        withContext(Dispatchers.IO) {
            try {
                val connection = DBconnection.getConnection()
                val appointmentQuery = AppointmentsQueries(connection)
                val vaccineQuery = VaccinesQueries(connection)
                val doctorQuery = DoctorsQueries(connection)

                val appointments = appointmentQuery.getAllAppointments()
                appointments?.forEach { appointment ->
                    if (appointment?.pesel == currentUserPesel) {
                        val vaccine = vaccineQuery.getVaccineById(appointment.vaccineId)
                        val doctor = doctorQuery.getDoctorById(appointment.doctorId)
                        if (vaccine != null && doctor != null) {
                            val vaccinationDetail = VaccinationDetail(
                                id = appointment.id ?: 0,
                                vaccineId = appointment.vaccineId,
                                pesel = appointment.pesel,
                                doctorName = doctor.name,
                                doctorSurname = doctor.surname,
                                date = appointment.date,
                                time = appointment.time,
                                address = appointment.address,
                                dose = appointment.dose,
                                vaccineName = vaccine.vaccineName
                            )
                            vaccinationList.add(vaccinationDetail)
                        }
                    }
                }
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return vaccinationList
    }

    /**
     * Fetch vaccination data from the database and update the RecyclerView
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun fetchVaccinationData() {
        val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid

        lifecycleScope.launch {
            var currentUserPesel: String? = null
            if (firebaseUid != null) {
                withContext(Dispatchers.IO) {
                    val connection = DBconnection.getConnection()
                    val patientsQuery = PatientsQueries(connection)
                    currentUserPesel = patientsQuery.getPeselByUID(firebaseUid)
                    connection.close()
                }
            }

            if (currentUserPesel != null) {
                vaccinationList = fetchVaccinationDataFromDB(currentUserPesel!!)
            }

            vaccinationList.sortBy { it.date }

            historyAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Update an appointment in the database
     * @param updatedAppointment The updated appointment
     */
    @SuppressLint("NotifyDataSetChanged")
    suspend fun updateAppointment(updatedAppointment: Appointments) {
        withContext(Dispatchers.IO) {
            try {
                val connection = DBconnection.getConnection()
                val appointmentQuery = AppointmentsQueries(connection)

                if (!isDateValid(updatedAppointment.date.toString())) {
                    showCoroutineToast("Invalid date format")
                    return@withContext
                }

                if (!isTimeValid(updatedAppointment.time.toString())) {
                    showCoroutineToast("Invalid time format")
                    return@withContext
                }

                if (updatedAppointment.address.isEmpty()) {
                    showCoroutineToast("Address cannot be empty")
                    return@withContext
                }

                val result = appointmentQuery
                    .updateAppointment(updatedAppointment.id ?: 0, updatedAppointment)

                if (!result) {
                    showCoroutineToast("Failed to update appointment")
                } else {
                    val updatedVaccination = vaccinationList.find { it.id == updatedAppointment.id }
                    updatedVaccination?.date = updatedAppointment.date
                    updatedVaccination?.time = updatedAppointment.time
                    updatedVaccination?.address = updatedAppointment.address
                    withContext(Dispatchers.Main) {
                        historyAdapter.notifyDataSetChanged()
                    }
                    showCoroutineToast("Appointment updated successfully")
                }
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Delete an appointment from the database
     * @param vaccination The vaccination detail to be deleted
     */
    @SuppressLint("NotifyDataSetChanged")
    suspend fun deleteAppointment(vaccination: VaccinationDetail) {
        withContext(Dispatchers.IO) {
            try {
                val connection = DBconnection.getConnection()
                val appointmentQuery = AppointmentsQueries(connection)

                val result = appointmentQuery.deleteAppointment(vaccination.id)

                if (result) {
                    vaccinationList.remove(vaccination)
                    withContext(Dispatchers.Main) {
                        historyAdapter.notifyDataSetChanged()
                    }
                } else {
                    showCoroutineToast("Failed to delete appointment")
                }
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Show a toast message
     * @param message The message to be displayed in the toast
     */
    private suspend fun showCoroutineToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@HistoryActivity, message, Toast.LENGTH_SHORT).show()
            delay(1000)
        }
    }

    /**
     * Check if a date is valid
     * @param date The date to be checked
     * @return Boolean indicating whether the date is valid
     */
    @SuppressLint("SimpleDateFormat")
    private fun isDateValid(date: String): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd")
        format.isLenient = false
        return try {
            format.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if a time is valid
     * @param time The time to be checked
     * @return Boolean indicating whether the time is valid
     */
    @SuppressLint("SimpleDateFormat")
    private fun isTimeValid(time: String): Boolean {
        val format = SimpleDateFormat("HH:mm")
        format.isLenient = false
        return try {
            format.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

}

