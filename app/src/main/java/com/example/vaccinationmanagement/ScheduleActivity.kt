package com.example.vaccinationmanagement

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class ScheduleActivity : AppCompatActivity() {

    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var etVaccineName: EditText
    private lateinit var selectedDate: Calendar
    private lateinit var selectedTime: Calendar
    private lateinit var btnSaveSchedule: Button
    private lateinit var btnEnterAddress: Button
    private var enteredAddress: String = ""
    private var dateString: String = ""
    private var timeString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        initViews()

        btnPickDate.setOnClickListener {
            showDatePickerDialog()
        }

        btnPickTime.setOnClickListener {
            showTimePickerDialog()
        }

        btnEnterAddress.setOnClickListener {
            showAddressEntryDialog()
        }

        btnSaveSchedule.setOnClickListener {
            saveSchedule()
        }
    }

    private fun initViews() {
        btnPickDate = findViewById(R.id.btnDatePicker)
        btnPickTime = findViewById(R.id.btnTimePicker)
        etVaccineName = findViewById(R.id.etVaccineName)
        selectedDate = Calendar.getInstance()
        selectedTime = Calendar.getInstance()
        btnSaveSchedule = findViewById(R.id.btnScheduleAppointment)
        btnEnterAddress = findViewById(R.id.btnEnterAddress)
    }

    private fun showDatePickerDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_date_picker)

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
        val btnSetDate = dialog.findViewById<Button>(R.id.btnSetDate)

        btnSetDate.setOnClickListener {
            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month
            val selectedDay = datePicker.dayOfMonth

            dateString = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            showToast("Selected Date: $dateString")

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showTimePickerDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_time_picker)

        val timePicker = dialog.findViewById<TimePicker>(R.id.timePicker)
        val btnSetTime = dialog.findViewById<Button>(R.id.btnSetTime)

        btnSetTime.setOnClickListener {
            val selectedHour = timePicker.currentHour
            val selectedMinute = timePicker.currentMinute

            val formattedHour = String.format("%02d", selectedHour)
            val formattedMinute = String.format("%02d", selectedMinute)

            timeString = "$formattedHour:$formattedMinute"
            showToast("Selected Time: $timeString")

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAddressEntryDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_address_entry)

        val etAddress = dialog.findViewById<EditText>(R.id.etAddress)
        val btnSaveAddress = dialog.findViewById<Button>(R.id.btnSaveAddress)

        btnSaveAddress.setOnClickListener {
            enteredAddress = etAddress.text.toString()
            showToast("Entered Address: $enteredAddress")

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getDose() {
        // TODO implement this function
        // check the dose of the vaccine by its name
        // fetch total doses from MySQL database and subtract the user doses assigned
        // to the particular vaccine
    }

    private fun getAvailableDoctors() {
        // TODO implement this function
        // fetch available doctors from MySQL database
        // for the time being it can be randomly selected doctor
        // for the future implementation - doctors who are available on the selected date and time
    }

    private fun saveSchedule() {
        // TODO implement this function
        // save the schedule to MySQL database
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}