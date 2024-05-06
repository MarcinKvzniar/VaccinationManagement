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
import com.example.vaccinationmanagement.vaccines.VaccinesQueries
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

    private suspend fun fetchVaccinationDataFromDB(): MutableList<VaccinationDetail> {
        withContext(Dispatchers.IO) {
            try {
                val connection = DBconnection.getConnection()
                val appointmentQuery = AppointmentsQueries(connection)
                val vaccineQuery = VaccinesQueries(connection)
                val doctorQuery = DoctorsQueries(connection)

                val appointments = appointmentQuery.getAllAppointments()
                appointments?.forEach { appointment ->
                    val vaccine = vaccineQuery.getVaccineById(appointment?.vaccineId ?: 0)
                    val doctor = doctorQuery.getDoctorById(appointment?.doctorId ?: 0)
                    if (vaccine != null && doctor != null) {
                        val vaccinationDetail = VaccinationDetail(
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
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return vaccinationList
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchVaccinationData() {
        lifecycleScope.launch {
            vaccinationList = fetchVaccinationDataFromDB()

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

                if (result) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HistoryActivity, "Appointment updated successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HistoryActivity, "Failed to update appointment", Toast.LENGTH_SHORT).show()
                    }
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

                val result = appointmentQuery
                    .deleteAppointment(vaccination.vaccineId)

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

    fun showUpdateDialog(vaccination: VaccinationDetail) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_update_appointment)

        val etDate = dialog.findViewById<EditText>(R.id.etDate)
        val etTime = dialog.findViewById<EditText>(R.id.etTime)
        val etAddress = dialog.findViewById<EditText>(R.id.etAddress)
        val btnUpdate = dialog.findViewById<Button>(R.id.btnUpdate)

        btnUpdate.setOnClickListener {
            val date = Date.valueOf(etDate.text.toString())
            val time = Time.valueOf(etTime.text.toString())
            val address = etAddress.text.toString()

            val updatedAppointment = Appointments(
                vaccineId = vaccination.vaccineId,
                pesel = vaccination.pesel,
                doctorId = 1, // TODO get doctor id
                date = date,
                time = time,
                address = address,
                dose = vaccination.dose
            )

            lifecycleScope.launch {
                updateAppointment(updatedAppointment)
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    private suspend fun showCoroutineToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@HistoryActivity, message, Toast.LENGTH_SHORT).show()
            delay(1000)
        }
    }

}

