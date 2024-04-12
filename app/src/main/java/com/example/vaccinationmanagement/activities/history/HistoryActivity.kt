package com.example.vaccinationmanagement.activities.history

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagement.activities.HomeActivity
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.appointments.Appointments

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBackHome: Button
    private var vaccinationList: MutableList<Appointments> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.historyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = HistoryAdapter(vaccinationList)
        recyclerView.adapter = historyAdapter

        btnBackHome.setOnClickListener {
            startActivity(
                Intent(this@HistoryActivity,
                HomeActivity::class.java)
            )
        }

        fetchVaccinationData()
    }

    private fun fetchVaccinationData() {
        //fetch vaccination data from database
    }
}