package com.example.vaccinationmanagement.history

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagement.HomeActivity
import com.example.vaccinationmanagement.R

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBackHome: Button
//    private var vaccinationList: MutableList<> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

//        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = HistoryAdapter() //add vaccination data as parameter
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