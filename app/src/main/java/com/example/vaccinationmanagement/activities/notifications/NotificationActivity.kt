package com.example.vaccinationmanagement.activities.notifications

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.dbConfig.DBconnection
import com.example.vaccinationmanagement.notifications.NotificationQueries
import com.example.vaccinationmanagement.vaccines.VaccinesQueries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class NotificationActivity : AppCompatActivity() {

    private lateinit var setDateButton: Button
    private lateinit var setTimeButton: Button
    private lateinit var btnSaveNotification: Button
    private lateinit var vaccineNameEditText: EditText

    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Initialize views
        setDateButton = findViewById(R.id.setDateButton)
        setTimeButton = findViewById(R.id.setTimeButton)
        btnSaveNotification = findViewById(R.id.scheduleNotificationButton)
        vaccineNameEditText = findViewById(R.id.etVaccineNameItem)

        // Set click listeners
        setDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        setTimeButton.setOnClickListener {
            showTimePickerDialog()
        }

        suspend fun showToast(message: String) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@NotificationActivity, message, Toast.LENGTH_SHORT).show()
                delay(1000)
            }
        }

        btnSaveNotification.setOnClickListener {
            if (selectedDate != null && selectedTime != null) {
                lifecycleScope.launch {
                    // Save the selected date and time to the database
                    saveNotification(selectedDate!!, selectedTime!!)
                }
            } else {
                lifecycleScope.launch {
                    showToast("Please select both date and time.")
                }
            }
        }

    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Format the selected date as a string
                selectedDate = String.format(
                    "%d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDayOfMonth
                )
            },
            year, month, dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Format the selected time as a string
                selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            },
            hour, minute, false
        )

        timePickerDialog.show()
    }

    private suspend fun checkIfVaccineExists(vaccineName: String): Boolean {
        return withContext(Dispatchers.IO) {
            var exists = false
            try {
                val connection = DBconnection.getConnection()
                val vaccineQuery = VaccinesQueries(connection)
                exists = vaccineQuery.doesVaccineExist(vaccineName)
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("NotificationActivity", "Error checking if vaccine exists", e)
            }
            exists
        }
    }

    private suspend fun saveNotification(date: String, time: String) {
        val vaccineName = vaccineNameEditText.text.toString().trim()
        if (!checkIfVaccineExists(vaccineName)) {
            showToast("Invalid vaccine name")
            return
        }
        val vaccineId = getVaccineIdByVaccineName(vaccineName)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        // Insert the notification into the database
        insertNotificationIntoDB(vaccineId, uid!!, date, time)
    }

    private suspend fun insertNotificationIntoDB(vaccineId: Int, uid: String, date: String, time: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val connection = DBconnection.getConnection()
                val notificationQuery = NotificationQueries(connection)
                val newNotification = Notification(
                    vaccineId = vaccineId,
                    uid = uid,
                    notificationDate = Date.valueOf(date),
                    notificationTime = Time.valueOf(time)
                )
                val insertSuccessful = notificationQuery.insertNotification(newNotification)
                connection.close()

                if (insertSuccessful) {
                    withContext(Dispatchers.Main) {
                        showToast("Notification scheduled successfully")
                    }
                } else {
                    showToast("Notification scheduling failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getVaccineIdByVaccineName(vaccineName: String): Int {
        return withContext(Dispatchers.IO) {
            var vaccineId = 0
            try {
                val connection = DBconnection.getConnection()
                val vaccineQuery = VaccinesQueries(connection)
                vaccineId = vaccineQuery.getVaccineIdByVaccineName(vaccineName)
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            vaccineId
        }
    }

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

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@NotificationActivity, message, Toast.LENGTH_SHORT).show()
            delay(1000)
        }
    }

}
