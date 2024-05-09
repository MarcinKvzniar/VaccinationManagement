package com.example.vaccinationmanagement.activities.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vaccinationmanagement.activities.HomeActivity
import com.example.vaccinationmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

/**
 * LoginActivity is an activity class that handles user login.
 * It extends AppCompatActivity, which is a base class for activities
 * that use the support library action bar features.
 */
class LoginActivity : AppCompatActivity() {

    // Declare UI elements and Firebase authentication instance
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: TextView
    private lateinit var inputEmailLog: EditText
    private lateinit var inputPasswordLog: EditText
    private lateinit var showPassword: CheckBox
    private lateinit var firebaseAuth: FirebaseAuth

    /**
     * This is the first callback and called when this activity is first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         * This function is called when the activity is starting.
         * It initializes the activity and its views.
         */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views and set click listeners
        initViews()

        // Navigate to RegisterActivity when register button is clicked
        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity,
                RegisterActivity::class.java))
        }

        // Attempt to log in the user when login button is clicked
        btnLogin.setOnClickListener {
            logInRegisteredUser()
        }
    }

    /**
     * Initialize views and Firebase authentication instance
     */
    private fun initViews() {
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)
        inputEmailLog = findViewById(R.id.inputEmailLog)
        inputPasswordLog = findViewById(R.id.inputPasswordLog)
        showPassword = findViewById(R.id.showPassword)

        // Set an OnCheckedChangeListener on the showPassword checkbox to toggle password visibility
        showPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                inputPasswordLog
                    .transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                inputPasswordLog
                    .transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        // Initialize Firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance()
    }

    /**
     * Validate login details
     * @return Boolean indicating whether the login details are valid
     */
    private fun validateLoginDetails(): Boolean {
        /**
         * This function validates the login details entered by the user.
         * It checks if the email and password fields are not empty.
         * @return Boolean Returns true if the details are valid, else false.
         */
        return when {
            TextUtils.isEmpty(inputEmailLog.text.toString().trim { it <= ' ' }) -> {
                showBasicToast(getString(R.string.err_msg_enter_email))
                false
            }
            TextUtils.isEmpty(inputPasswordLog.text.toString().trim()) -> {
                showBasicToast(getString(R.string.err_msg_enter_password))
                false
            }
            else -> true
        }
    }

    /**
     * Log in the registered user
     */
    private fun logInRegisteredUser() {
        /**
         * This function logs in the registered user.
         * It validates the login details and if they are valid, it logs in the user.
         */
        if (validateLoginDetails()) {
            val email = inputEmailLog.text.toString().trim()
            val password = inputPasswordLog.text.toString().trim()

            // Attempt to sign in with the provided email and password
            FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showBasicToast("Logged in successfully.")
                        goToHomeActivity()
                        finish()
                    } else {
                        // Handle possible exceptions during the sign-in process
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthInvalidUserException -> {
                                getString(R.string.err_msg_user_not_found)
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                getString(R.string.err_msg_wrong_password)
                            }
                            else -> {
                                getString(R.string.err_msg_unknown_error)
                            }
                        }
                        showBasicToast(errorMessage)
                    }
                }
        }
    }

    /**
     * Navigate to HomeActivity
     */
    private fun goToHomeActivity() {
        /**
         * This function navigates the user to the HomeActivity.
         */
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("uID", uid)
        startActivity(intent)
    }

    /**
     * Show a basic Toast message
     * @param message The message to be displayed in the Toast
     */
    private fun showBasicToast(message: String) {
        /**
         * This function shows a basic toast message.
         * @param message The message to be shown in the toast.
         */
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}