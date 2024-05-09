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

/**
 * HomeActivity is an activity class that handles the home screen of the application.
 * It extends AppCompatActivity, which is a base class for activities
 * that use the support library action bar features.
 */
class HomeActivity : AppCompatActivity() {

    // Declare UI elements and UserNotificationService instance
    private lateinit var btnSchedule : Button
    private lateinit var btnHistory: Button
    private lateinit var btnLogOut : Button
    private lateinit var btnNotifications: Button
    private lateinit var userNotificationService: UserNotificationService

    /**
     * This is the first callback and called when this activity is first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize views and set click listeners
        initViews()

        val appointmentsDAO = AppointmentsDAOImpl()
        userNotificationService = UserNotificationService(this, appointmentsDAO)
        userNotificationService.notificationHelper.createNotificationChannel()

        // Navigate to ScheduleActivity when schedule button is clicked
        btnSchedule.setOnClickListener {
            startActivity(
                Intent(this@HomeActivity,
                ScheduleActivity::class.java))
        }

        // Navigate to HistoryActivity when history button is clicked
        btnHistory.setOnClickListener {
            startActivity(
                Intent(this@HomeActivity,
                HistoryActivity::class.java))
        }

        // Navigate to LoginActivity when logout button is clicked
        btnLogOut.setOnClickListener {
            startActivity(
                Intent(this@HomeActivity,
                LoginActivity::class.java))
        }

        // Navigate to NotificationActivity when notifications button is clicked
        btnNotifications.setOnClickListener {
           startActivity(
               Intent(this@HomeActivity,
                NotificationActivity::class.java)
           )
        }

    }

    /**
     * Initialize views
     */
    private fun initViews() {
        btnSchedule = findViewById(R.id.btnSchedule)
        btnHistory = findViewById(R.id.btnHistory)
        btnLogOut = findViewById(R.id.btnLogOut)
        btnNotifications = findViewById(R.id.btnNotifications)
    }
}