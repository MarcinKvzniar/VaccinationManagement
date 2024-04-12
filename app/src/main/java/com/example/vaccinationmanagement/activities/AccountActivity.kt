package com.example.vaccinationmanagement.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.dbConfig.DBconnection
import com.example.vaccinationmanagement.patients.Patients
import com.example.vaccinationmanagement.patients.PatientsQueries
import java.sql.Date

class AccountActivity : AppCompatActivity() {

    private lateinit var etPesel: EditText
    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etDateOfBirth: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        initViews()

        btnSave.setOnClickListener {
            val pesel = etPesel.text.toString()
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            val dateOfBirth = Date.valueOf(etDateOfBirth.text.toString())

            saveDetails(pesel, name, surname, dateOfBirth)
        }
    }

    private fun initViews() {
        etPesel = findViewById(R.id.edit_pesel)
        etName = findViewById(R.id.edit_name)
        etSurname = findViewById(R.id.edit_surname)
        etDateOfBirth = findViewById(R.id.edit_dob)
        btnSave = findViewById(R.id.btn_save)
    }

    private fun saveDetails(pesel: String, name: String, surname: String, dateOfBirth: Date) {
        // TODO some security checks, anyone can edit someone else account
        // TODO maybe add ROLE to the database ('USER', 'DOCTOR', 'ADMIN') and check if user is 'USER'
        val connection = DBconnection.getConnection()
        val patientQuery = PatientsQueries(connection)

        val patient = Patients(pesel, name, surname, dateOfBirth)

        if (patientQuery.getPatientByPesel(pesel) != null) {
            patientQuery.updatePatient(pesel, patient)
        } else {
            patientQuery.insertPatient(patient)
        }
    }
}