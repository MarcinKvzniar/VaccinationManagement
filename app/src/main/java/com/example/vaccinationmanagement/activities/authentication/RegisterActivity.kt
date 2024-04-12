package com.example.vaccinationmanagement.activities.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vaccinationmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnRegister: Button
    private lateinit var btnGoToLogin: TextView

    private lateinit var inputNameReg: EditText
    private lateinit var inputEmailReg: EditText
    private lateinit var inputPasswordReg: EditText
    private lateinit var inputRepPasswordReg: EditText

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()

        btnGoToLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity,
                LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            if (validateRegisterDetails()) {
                registerUser()
            }
        }
    }

    private fun initViews() {
        btnRegister = findViewById(R.id.btnRegister)
        btnGoToLogin = findViewById(R.id.btnGoToLogin)

        inputNameReg = findViewById(R.id.inputName)
        inputEmailReg = findViewById(R.id.inputEmailReg)
        inputPasswordReg = findViewById(R.id.inputPasswordReg)
        inputRepPasswordReg = findViewById(R.id.inputRepeatPasswordReg)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun validateRegisterDetails(): Boolean {
        val specialChars = "!@#$%^&*-_+(){}/[]|".toCharArray()

        return when {
            TextUtils.isEmpty(inputNameReg.text.toString().trim { it <= ' ' }) -> {
                showBasicToast(resources.getString(R.string.err_msg_enter_name))
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

    private fun registerUser() {
        val login = inputEmailReg.text.toString().trim()
        val password = inputPasswordReg.text.toString().trim()

        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(login, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result?.user!!
                    showBasicToast("You are registered successfully." +
                            " Your user id is ${firebaseUser.uid}")

                    /**
                     * TODO implement registering user with firestore and providing the data
                     * TODO to mysql database
                     */

                    startActivity(
                        Intent(this@RegisterActivity,
                        LoginActivity::class.java)
                    )
                    finish()
                } else {
                    showBasicToast(task.exception?.message.toString())
                }
            }
    }

    private fun showBasicToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun userRegistrationSuccess() {
        Toast.makeText(this@RegisterActivity, getString(R.string.register_success),
            Toast.LENGTH_LONG).show()
    }


}
