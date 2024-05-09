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

class LoginActivity : AppCompatActivity() {

    /*
     * LoginActivity is an activity that provides the user with a login interface.
     * It validates the user's input and logs in the user if the input is valid.
     */

    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: TextView

    private lateinit var inputEmailLog: EditText
    private lateinit var inputPasswordLog: EditText

    private lateinit var showPassword: CheckBox
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         * This function is called when the activity is starting.
         * It initializes the activity and its views.
         */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()

        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity,
                RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            logInRegisteredUser()
        }
    }

    private fun initViews() {
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)

        inputEmailLog = findViewById(R.id.inputEmailLog)
        inputPasswordLog = findViewById(R.id.inputPasswordLog)

        showPassword = findViewById(R.id.showPassword)

        // Show/hide password
        showPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                inputPasswordLog
                    .transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                inputPasswordLog
                    .transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        firebaseAuth = FirebaseAuth.getInstance()

    }

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

    private fun logInRegisteredUser() {
        /**
         * This function logs in the registered user.
         * It validates the login details and if they are valid, it logs in the user.
         */
        if (validateLoginDetails()) {
            val email = inputEmailLog.text.toString().trim()
            val password = inputPasswordLog.text.toString().trim()

            FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showBasicToast("Logged in successfully.")
                        goToHomeActivity()
                        finish()
                    } else {
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

    private fun goToHomeActivity() {
        /**
         * This function navigates the user to the HomeActivity.
         */
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("uID", uid)
        startActivity(intent)
    }

    private fun showBasicToast(message: String) {
        /**
         * This function shows a basic toast message.
         * @param message The message to be shown in the toast.
         */
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}