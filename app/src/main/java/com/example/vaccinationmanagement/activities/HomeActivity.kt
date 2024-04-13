package com.example.vaccinationmanagement.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.activities.authentication.LoginActivity
import com.example.vaccinationmanagement.activities.history.HistoryActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var btnSchedule : Button
    private lateinit var btnHistory: Button
    private lateinit var btnLogOut : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()


        btnSchedule.setOnClickListener {
            startActivity(
                Intent(this@HomeActivity,
                ScheduleActivity::class.java))
        }

        btnHistory.setOnClickListener {
            startActivity(
                Intent(this@HomeActivity,
                HistoryActivity::class.java))
        }

        btnLogOut.setOnClickListener {
            startActivity(
                Intent(this@HomeActivity,
                LoginActivity::class.java))
        }

    }

    private fun initViews() {
        btnSchedule = findViewById(R.id.btnSchedule)
        btnHistory = findViewById(R.id.btnHistory)
        btnLogOut = findViewById(R.id.btnLogOut)
    }
}