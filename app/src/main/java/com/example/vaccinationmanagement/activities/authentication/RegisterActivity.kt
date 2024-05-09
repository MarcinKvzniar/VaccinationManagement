package com.example.vaccinationmanagement.activities.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.dbConfig.DBconnection
import com.example.vaccinationmanagement.patients.Patients
import com.example.vaccinationmanagement.patients.PatientsQueries
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat

/**
 * RegisterActivity is an activity class that handles user registration.
 * It extends AppCompatActivity, which is a base class for activities
 * that use the support library action bar features.
 */
class RegisterActivity : AppCompatActivity() {

    // Declare UI elements and Firebase authentication instance
    private lateinit var btnRegister: Button
    private lateinit var btnGoToLogin: TextView
    private lateinit var inputNameReg: EditText
    private lateinit var inputSurnameReg: EditText
    private lateinit var inputDateOfBirthReg: EditText
    private lateinit var inputPeselReg: EditText
    private lateinit var inputEmailReg: EditText
    private lateinit var inputPasswordReg: EditText
    private lateinit var inputRepPasswordReg: EditText
    private lateinit var firebaseAuth: FirebaseAuth

    /**
     * This is the first callback and called when this activity is first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views and set click listeners
        initViews()

        // Navigate to LoginActivity when login button is clicked
        btnGoToLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity,
                LoginActivity::class.java))
        }

        // Attempt to register the user when register button is clicked
        btnRegister.setOnClickListener {
            if (validateRegisterDetails()) {
                registerUser()
            }
        }
    }

    /**
     * Initialize views and Firebase authentication instance
     */
    private fun initViews() {
        btnRegister = findViewById(R.id.btnRegister)
        btnGoToLogin = findViewById(R.id.btnGoToLogin)
        inputNameReg = findViewById(R.id.inputName)
        inputSurnameReg = findViewById(R.id.inputSurnameReg)
        inputDateOfBirthReg = findViewById(R.id.inputDateOfBirthReg)
        inputPeselReg = findViewById(R.id.inputPesel)
        inputEmailReg = findViewById(R.id.inputEmailReg)
        inputPasswordReg = findViewById(R.id.inputPasswordReg)
        inputRepPasswordReg = findViewById(R.id.inputRepeatPasswordReg)

        // Initialize Firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance()
    }

    /**
     * Validate registration details
     * @return Boolean indicating whether the registration details are valid
     */
    private fun validateRegisterDetails(): Boolean {
        val specialChars = "!@#$%^&*-_+(){}/[]|".toCharArray()

        return when {
            TextUtils.isEmpty(inputNameReg.text.toString().trim { it <= ' ' }) -> {
                showBasicToast(resources.getString(R.string.err_msg_enter_name))
                false
            }
            TextUtils.isEmpty(inputSurnameReg.text.toString().trim { it <= ' ' }) -> {
                showBasicToast(resources.getString(R.string.err_msg_enter_surname))
                false
            }
            TextUtils.isEmpty(inputDateOfBirthReg.text.toString().trim { it <= ' ' }) -> {
                showBasicToast(resources.getString(R.string.err_msg_enter_date_of_birth))
                false
            }
            !isDateValid(inputDateOfBirthReg.text.toString().trim { it <= ' ' }) -> {
                showBasicToast(resources.getString(R.string.err_msg_date_of_birth_format))
                false
            }
            TextUtils.isEmpty(inputPeselReg.text.toString().trim { it <= ' ' }) -> {
                showBasicToast(resources.getString(R.string.err_msg_enter_pesel))
                false
            }
            TextUtils.isEmpty(inputEmailReg.text.toString().trim { it <= ' ' }) -> {
                showBasicToast(resources.getString(R.string.err_msg_enter_email))
                false
            }
            TextUtils.isEmpty(inputPasswordReg.text.toString().trim { it <= ' ' }) -> {
                showBasicToast(resources.getString(R.string.err_msg_enter_password))
                false
            }
            TextUtils.isEmpty(inputRepPasswordReg.text.toString().trim { it <= ' ' }) -> {
                showBasicToast(resources.getString(R.string.err_msg_enter_reppassword))
                false
            }
            inputPasswordReg.text.toString().trim { it <= ' ' }.length < 8 -> {
                showBasicToast(resources.getString(R.string.err_msg_password_length))
                false
            }
            !(inputPasswordReg.text.toString().trim { it <= ' ' }.any {
                    char -> char in specialChars}) -> {
                showBasicToast(resources.getString(R.string.err_msg_password_special_chars))
                false
            }
            inputPasswordReg.text.toString().trim { it <= ' ' } !=
                    inputRepPasswordReg.text.toString().trim { it <= ' ' } -> {
                showBasicToast(resources.getString(R.string.err_msg_password_mismatch))
                false
            }
            else -> true
        }
    }

    /**
     * Register the user
     */
    private fun registerUser() {
        val email = inputEmailReg.text.toString().trim()
        val name = inputNameReg.text.toString().trim()
        val surname = inputSurnameReg.text.toString().trim()
        val dateOfBirth = inputDateOfBirthReg.text.toString().trim()
        val pesel = inputPeselReg.text.toString().trim()
        val password = inputPasswordReg.text.toString().trim()

        // Attempt to create a new user with the provided email and password
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result?.user!!
                    val uid = firebaseUser.uid

                    // Insert the new user into the database
                    insertPatientIntoDB(pesel, uid, name, surname, Date.valueOf(dateOfBirth))
                } else {
                    // Show a toast message if the registration failed
                    userRegistrationFailure()
                }
            }
    }

    /**
     * Insert a new patient into the database
     * @param pesel The patient's PESEL number
     * @param uid The patient's user ID
     * @param name The patient's name
     * @param surname The patient's surname
     * @param dateOfBirth The patient's date of birth
     */
    private fun insertPatientIntoDB(
        pesel: String,
        uid: String,
        name: String,
        surname: String,
        dateOfBirth: Date
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val connection = DBconnection.getConnection()
                val patientsQueries = PatientsQueries(connection)
                val newPatient = Patients(pesel, uid, name, surname, dateOfBirth)
                val insertSuccessful = patientsQueries.insertPatient(newPatient)
                connection.close()

                if (insertSuccessful) {
                    // Navigate to LoginActivity if the insertion was successful
                    startActivity(
                        Intent(this@RegisterActivity,
                            LoginActivity::class.java)
                    )
                    finish()
                } else {
                    // Show a toast message if the insertion failed
                    userRegistrationFailure()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Show a basic Toast message
     * @param message The message to be displayed in the Toast
     */
    private fun showBasicToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Show a toast message indicating that the user registration failed
     */
    private fun userRegistrationFailure() {
        Toast.makeText(this@RegisterActivity, getString(R.string.register_failure),
            Toast.LENGTH_LONG).show()
    }

    /**
     * Check if a date is valid
     * @param date The date to be checked
     * @return Boolean indicating whether the date is valid
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
}