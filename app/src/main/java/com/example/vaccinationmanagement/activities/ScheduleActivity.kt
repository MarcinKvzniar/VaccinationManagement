package com.example.vaccinationmanagement.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * ScheduleActivity is an activity class that handles the scheduling of vaccinations.
 * It extends AppCompatActivity, which is a base class for activities that use the support library action bar features.
 */
class ScheduleActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var etVaccineName: EditText
    private lateinit var selectedDate: Calendar
    private lateinit var selectedTime: Calendar
    private lateinit var btnSaveSchedule: Button
    private lateinit var btnEnterAddress: Button
    private lateinit var btnGetDoctor: Button
    private lateinit var btnGetDose: Button
    private var enteredAddress: String = ""
    private var dateString: String = ""
    private var timeString: String = ""

    /**
     * This is the first callback and called when this activity is first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        // Initialize views and set click listeners
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
            lifecycleScope.launch {
                getAvailableDoctors()
            }
        }

        btnGetDose.setOnClickListener {
            lifecycleScope.launch {
                getDose()
            }
        }

        btnSaveSchedule.setOnClickListener {
            lifecycleScope.launch {
                saveSchedule()
            }
        }
    }

    /**
     * Initialize views
     */
    private fun initViews() {
        btnPickDate = findViewById(R.id.btnDatePicker)
        btnPickTime = findViewById(R.id.btnTimePicker)
        etVaccineName = findViewById(R.id.etVaccineName)
        selectedDate = Calendar.getInstance()
        selectedTime = Calendar.getInstance()
        btnSaveSchedule = findViewById(R.id.btnScheduleAppointment)
        btnEnterAddress = findViewById(R.id.btnEnterAddress)
        btnGetDoctor = findViewById(R.id.btnGetTheDoctor)
        btnGetDose = findViewById(R.id.btnGetDose)
    }

    /**
     * Show a dialog for the user to pick a date
     */
    private fun showDatePickerDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_date_picker)

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
        val btnSetDate = dialog.findViewById<Button>(R.id.btnSetDate)

        btnSetDate.setOnClickListener {
            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month + 1
            val selectedDay = datePicker.dayOfMonth

            dateString = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay)

            lifecycleScope.launch {("Selected Date: $dateString")}

            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Show a dialog for the user to pick a time
     */
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

            timeString = "$formattedHour:$formattedMinute:00"
            lifecycleScope.launch { showToast("Selected Time: $timeString") }

            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Show a dialog for the user to enter an address
     */
    private fun showAddressEntryDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_address_entry)

        val etAddress = dialog.findViewById<EditText>(R.id.etAddress)
        val btnSaveAddress = dialog.findViewById<Button>(R.id.btnSaveAddress)

        btnSaveAddress.setOnClickListener {
            enteredAddress = etAddress.text.toString()
            lifecycleScope.launch { showToast("Selected Date: $dateString") }

            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Save the schedule to the database
     */
    private suspend fun saveSchedule() {
        val vaccineName = etVaccineName.text.toString().trim()
        if (!checkIfVaccineExists(vaccineName)) {
            showToast("Invalid vaccine name")
            return
        }
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

    /**
     * Insert the appointment into the database
     */
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
                    withContext(Dispatchers.Main) {
                        showToast("Appointment scheduled successfully")
                    }
                    startActivity(
                        Intent(this@ScheduleActivity,
                            HomeActivity::class.java)
                    )
                    finish()
                } else {
                    showToast("Appointment scheduling failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Get the vaccine ID by the vaccine name
     */
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

    /**
     * Get the patient's PESEL number by their user ID
     */
    private suspend fun getPatientPesel(uid: String?): String {
        return withContext(Dispatchers.IO) {
            var pesel = ""
            try {
                val connection = DBconnection.getConnection()
                val patientsQuery = PatientsQueries(connection)
                pesel = patientsQuery.getPeselByUID(uid) ?: ""
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            pesel
        }
    }

    /**
     * Check if a vaccine exists in the database
     */
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
                Log.e("ScheduleActivity", "Error checking if vaccine exists", e)
            }
            exists
        }
    }

    /**
     * Get the available doctors from the database
     */
    private suspend fun getAvailableDoctors(): Int {
        var doctorId = 0
        var doctorName: String? = null
        var doctorSurname: String? = null

        withContext(Dispatchers.IO) {
            try {
                val connection = DBconnection.getConnection()
                val doctorQuery = DoctorsQueries(connection)
                val doctors = doctorQuery.getAllDoctors()
                connection.close()

                if (doctors != null) {
                    val doctor = doctors.random()
                    doctorName = doctor?.name
                    doctorSurname = doctor?.surname
                    doctorId = doctor?.id ?: 0
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (doctorName != null && doctorSurname != null) {
            showToast("Doctor: $doctorName $doctorSurname")
        } else {
            showToast("No doctors available")
        }

        return doctorId
    }

    /**
     * Get the dose number for the vaccine
     */
    private suspend fun getDose(): Int {
        return withContext(Dispatchers.IO) {
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
            doseNumber
        }
    }

    /**
     * Check if a date string is valid
     */
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

    /**
     * Check if a time string is valid
     */
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

    /**
     * Show a toast message
     */
   private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@ScheduleActivity, message, Toast.LENGTH_SHORT).show()
            delay(1000)
        }
    }
}