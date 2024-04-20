package com.example.vaccinationmanagement.activities

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
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.appointments.Appointments
import com.example.vaccinationmanagement.appointments.AppointmentsQueries
import com.example.vaccinationmanagement.dbConfig.DBconnection
import com.example.vaccinationmanagement.doctors.DoctorsQueries
import com.example.vaccinationmanagement.patients.Patients
import com.example.vaccinationmanagement.patients.PatientsQueries
import com.example.vaccinationmanagement.vaccines.VaccinesDAO
import com.example.vaccinationmanagement.vaccines.VaccinesQueries
import com.google.firebase.auth.FirebaseAuth
import java.sql.Date
import java.sql.Time
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

        btnSaveSchedule.setOnClickListener {
            getDose()

            Handler(Looper.getMainLooper()).postDelayed({
                saveSchedule()
            }, 2000)
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

            if (!doctors.isNullOrEmpty()) {
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
        try {
            val connection = DBconnection.getConnection()
            val vaccineQuery = VaccinesQueries(connection)
            val appointmentsQuery = AppointmentsQueries(connection)

            val vaccineId = vaccineQuery.getVaccineIdByVaccineName(etVaccineName.text.toString())
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            uid?.let {
                val pesel = PatientsQueries(connection).getPeselByUID(it)
                if (pesel != null) {
                    val doctorId = getAvailableDoctors()
                    val date = Date.valueOf(dateString)
                    val time = Time.valueOf(timeString)
                    val address = enteredAddress
                    val dose = getDose()

                    val newAppointment = Appointments(
                        vaccineId = vaccineId,
                        pesel = pesel,
                        doctorId = doctorId,
                        date = date,
                        time = time,
                        address = address,
                        dose = dose
                    )

                    appointmentsQuery.insertAppointment(newAppointment)
                } else {
                    showToast("Logged patient not found")
                }
            }
            connection.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}