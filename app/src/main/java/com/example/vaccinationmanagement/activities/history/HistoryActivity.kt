package com.example.vaccinationmanagement.activities.history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.activities.HomeActivity
import com.example.vaccinationmanagement.appointments.AppointmentsQueries
import com.example.vaccinationmanagement.dbConfig.DBconnection
import com.example.vaccinationmanagement.doctors.DoctorsQueries
import com.example.vaccinationmanagement.vaccines.VaccinesQueries
import kotlinx.coroutines.Dispatchers
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
                Intent(this@HistoryActivity,
                HomeActivity::class.java)
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
}