package com.example.vaccinationmanagement.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.vaccinationmanagement.R

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
            val dateOfBirth = etDateOfBirth.text.toString()

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

    private fun saveDetails(pesel: String, name: String, surname: String, dateOfBirth: String) {
        // Save details to the mysql database
    }
}