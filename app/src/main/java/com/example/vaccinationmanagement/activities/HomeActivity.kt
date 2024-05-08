package com.example.vaccinationmanagement.activities

import com.example.vaccinationmanagement.activities.notifications.NotificationActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.activities.authentication.LoginActivity
import com.example.vaccinationmanagement.activities.history.HistoryActivity
import com.example.vaccinationmanagement.activities.notifications.UserNotificationService
import com.example.vaccinationmanagement.appointments.AppointmentsDAOImpl

class HomeActivity : AppCompatActivity() {

    private lateinit var btnSchedule : Button
    private lateinit var btnHistory: Button
    private lateinit var btnLogOut : Button
    private lateinit var btnNotifications: Button
    private lateinit var userNotificationService: UserNotificationService
    private lateinit var btnAccount: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()

        val appointmentsDAO = AppointmentsDAOImpl()
        userNotificationService = UserNotificationService(this, appointmentsDAO)
        userNotificationService.notificationHelper.createNotificationChannel()


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
        btnAccount.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AccountActivity::class.java))
        }

        btnNotifications.setOnClickListener {
           startActivity(
               Intent(this@HomeActivity,
                NotificationActivity::class.java)
           )
        }

    }

    private fun initViews() {
        btnSchedule = findViewById(R.id.btnSchedule)
        btnHistory = findViewById(R.id.btnHistory)
        btnLogOut = findViewById(R.id.btnLogOut)
        btnNotifications = findViewById(R.id.btnNotifications)
        btnAccount = findViewById(R.id.btnAccount)
    }
}