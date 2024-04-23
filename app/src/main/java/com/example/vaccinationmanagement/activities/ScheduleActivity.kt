package com.example.vaccinationmanagement.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.appointments.Appointments
import com.example.vaccinationmanagement.appointments.AppointmentsQueries
import com.example.vaccinationmanagement.dbConfig.DBconnection
import com.example.vaccinationmanagement.doctors.DoctorsQueries
import com.example.vaccinationmanagement.patients.PatientsQueries
import com.example.vaccinationmanagement.vaccines.VaccinesQueries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar

class ScheduleActivity : AppCompatActivity() {

    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var etVaccineName: EditText
    private lateinit var selectedDate: Calendar
    private lateinit var selectedTime: Calendar
    private lateinit var btnSaveSchedule: Button
    private lateinit var btnEnterAddress: Button
    private lateinit var btnGetDoctor: Button
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

        btnGetDoctor.setOnClickListener {
            getAvailableDoctors()
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
        btnGetDoctor = findViewById(R.id.btnGetTheDoctor)
    }

    private fun showDatePickerDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_date_picker)

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
        val btnSetDate = dialog.findViewById<Button>(R.id.btnSetDate)

        btnSetDate.setOnClickListener {
            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month + 1
            val selectedDay = datePicker.dayOfMonth

            dateString = if (selectedMonth < 10)
                "$selectedYear-0$selectedMonth-$selectedDay"
            else
                "$selectedYear-$selectedMonth-$selectedDay"

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

    private fun getDose(): Int {
        val vaccineName = etVaccineName.text.toString()
        var doseNumber = 0
        try {
            val connection = DBconnection.getConnection()
            val vaccineQuery = VaccinesQueries(connection)
            val totalDoses = vaccineQuery.getDosesByVaccineName(vaccineName)
            val appointmentCount = vaccineQuery.getAppointmentsCountForVaccine(vaccineName)

            doseNumber = appointmentCount + 1

            if (doseNumber <= totalDoses) {
                showToast("Dose number: $doseNumber / $totalDoses")
            } else {
                showToast("All doses have been administered")
            }
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return doseNumber
    }

    private fun getAvailableDoctors(): Int {
        var doctorId = 0
        try {
            val connection = DBconnection.getConnection()
            val doctorQuery = DoctorsQueries(connection)
            val doctors = doctorQuery.getAllDoctors()
            connection.close()

            if (doctors != null) {
                val doctor = doctors.random()
                showToast("Doctor: ${doctor?.name} ${doctor?.surname}")
                doctorId = doctor?.id ?: 0
            } else {
                showToast("No doctors available")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return doctorId
    }

    private fun saveSchedule() {
        val vaccineName = etVaccineName.text.toString().trim()

        val vaccineId = getVaccineIdByVaccineName(vaccineName)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val pesel = getPatientPesel(uid)
        val doctorId = getAvailableDoctors()

        val date = Date.valueOf(dateString)
        if (!isDateValid(dateString)) {
            showToast("Invalid date format")
            return
        }

        val time = Time.valueOf(timeString)
        if (!isTimeValid(timeString)) {
            showToast("Invalid time format")
            return
        }

        val address = enteredAddress
        val dose = getDose()

        insertAppointmentIntoDB(vaccineId, pesel, doctorId, date, time, address, dose)
    }

    private fun insertAppointmentIntoDB(vaccineId: Int, pesel: String, doctorId: Int, date: Date, time: Time, address: String, dose: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val connection = DBconnection.getConnection()
                val appointmentQuery = AppointmentsQueries(connection)
                val newAppointment = Appointments(
                    vaccineId = vaccineId,
                    pesel = pesel,
                    doctorId = doctorId,
                    date = date,
                    time = time,
                    address = address,
                    dose = dose
                )
                val insertSuccessful = appointmentQuery.insertAppointment(newAppointment)
                connection.close()

                if (insertSuccessful) {
                    showToast("Appointment scheduled successfully")
                } else {
                    showToast("Appointment scheduling failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getVaccineIdByVaccineName(vaccineName: String): Int {
        var vaccineId = 0
        try {
            val connection = DBconnection.getConnection()
            val vaccineQuery = VaccinesQueries(connection)
            vaccineId = vaccineQuery.getVaccineIdByVaccineName(vaccineName)
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return vaccineId
    }

    private fun getPatientPesel(uid: String?): String {
        var pesel = ""
        try {
            val connection = DBconnection.getConnection()
            val patientsQuery = PatientsQueries(connection)
            pesel = patientsQuery.getPeselByUID(uid) ?: ""
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pesel
    }

    private fun checkIfVaccineExists(vaccineName: String): Boolean {
        var exists = false
        try {
            val connection = DBconnection.getConnection()
            val vaccineQuery = VaccinesQueries(connection)
            val vaccine = vaccineQuery.getVaccineByName(vaccineName)
            connection.close()
            exists = vaccine != null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return exists
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}