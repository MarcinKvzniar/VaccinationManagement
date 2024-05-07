package com.example.vaccinationmanagement.activities.history

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import java.sql.Date
import java.sql.Time

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBackHome: Button
    private var vaccinationList: MutableList<VaccinationDetail> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

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

        fetchVaccinationData()
    }

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
                        val vaccine = vaccineQuery.getVaccineById(appointment?.vaccineId ?: 0)
                        val doctor = doctorQuery.getDoctorById(appointment?.doctorId ?: 0)
                        if (vaccine != null && doctor != null) {
                            val vaccinationDetail = VaccinationDetail(
                                id = appointment?.id ?: 0,
                                vaccineId = appointment?.vaccineId ?: 0,
                                pesel = appointment?.pesel ?: "",
                                doctorName = doctor.name,
                                doctorSurname = doctor.surname,
                                date = appointment?.date ?: Date(java.util.Date().time),
                                time = appointment?.time ?: Time(0),
                                address = appointment?.address ?: "",
                                dose = appointment?.dose ?: 0,
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

    suspend fun updateAppointment(updatedAppointment: Appointments) {
        withContext(Dispatchers.IO) {
            try {
                val connection = DBconnection.getConnection()
                val appointmentQuery = AppointmentsQueries(connection)

                val result = appointmentQuery.updateAppointment(updatedAppointment.id ?: 0, updatedAppointment)

                if (!result) {
                    showCoroutineToast("Failed to update appointment")
                }
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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

    private suspend fun showCoroutineToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@HistoryActivity, message, Toast.LENGTH_SHORT).show()
            delay(1000)
        }
    }

}

