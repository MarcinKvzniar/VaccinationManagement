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
 * ScheduleActivity is an activity that allows the user to schedule an appointment.
 * It provides an interface for the user to select a date, time, and address for the appointment.
 * The selected date, time, and address are then saved to the database.
 */
class ScheduleActivity : AppCompatActivity() {

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

    private suspend fun saveSchedule() {
        /**
         * This function saves the schedule created by the user.
         * It validates the entered details and if they are valid, it saves the schedule to the database.
         */
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
     * This function inserts an appointment into the database.
     * @param vaccineId The ID of the vaccine for the appointment.
     * @param pesel The PESEL number of the user.
     * @param doctorId The ID of the doctor for the appointment.
     * @param date The date of the appointment.
     * @param time The time of the appointment.
     * @param address The address of the appointment.
     * @param dose The dose number of the vaccine.
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

    private suspend fun getVaccineIdByVaccineName(vaccineName: String): Int {
        /**
         * This function retrieves the ID of a vaccine by its name.
         * @param vaccineName The name of the vaccine.
         * @return Int The ID of the vaccine.
         */
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

    private suspend fun getPatientPesel(uid: String?): String {
        /**
         * This function retrieves the PESEL number of a patient by their UID.
         * @param uid The UID of the patient.
         * @return String The PESEL number of the patient.
         */
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

    private suspend fun checkIfVaccineExists(vaccineName: String): Boolean {
        /**
         * This function checks if a vaccine exists in the database.
         * @param vaccineName The name of the vaccine to check.
         * @return Boolean Returns true if the vaccine exists, else false.
         */
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

    private suspend fun getAvailableDoctors(): Int {
        /**
         * This function retrieves the ID of an available doctor.
         * @return Int The ID of the doctor.
         */
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

    private suspend fun getDose(): Int {
        /**
         * This function retrieves the dose number of a vaccine.
         * @return Int The dose number of the vaccine.
         */
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

    @SuppressLint("SimpleDateFormat")
    /**
     * This function checks if a date is valid.
     * @param date The date to check.
     * @return Boolean Returns true if the date is valid, else false.
     */
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
    /**
     * This function checks if a time is valid.
     * @param time The time to check.
     * @return Boolean Returns true if the time is valid, else false.
     */
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
            Toast.makeText(this@ScheduleActivity, message, Toast.LENGTH_SHORT).show()
            delay(1000)
        }
    }
}