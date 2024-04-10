// Import statements
package com.tracker.expensemanager

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tracker.expensemanager.R
import com.google.firebase.auth.FirebaseAuth

// MainActivity class
class MainActivity : AppCompatActivity() {
    // Properties declaration
    private lateinit var mEmail: EditText
    private lateinit var mPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var mForgetPassword: TextView
    private lateinit var mSignupHere: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDialog: ProgressDialog

    // onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance()

        // Progress dialog initialization
        mDialog = ProgressDialog(this)

        // Initialize UI elements
        mEmail = findViewById(R.id.email_login)
        mPass = findViewById(R.id.password_login)
        btnLogin = findViewById(R.id.btn_login)
        mForgetPassword = findViewById(R.id.forget_password)
        mSignupHere = findViewById(R.id.signup_reg)

        // Set click listeners for login button
        btnLogin.setOnClickListener {
            // Get email and password input
            val email = mEmail.text.toString().trim()
            val pass = mPass.text.toString().trim()

            // Validate email and password fields
            if (TextUtils.isEmpty(email)) {
                mEmail.error = "Email Required.."
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(pass)) {
                mPass.error = "Password is Required.."
                return@setOnClickListener
            }

            // Display progress dialog
            mDialog.setMessage("Processing..")
            mDialog.show()

            // Sign in with email and password
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                mDialog.dismiss()
                if (task.isSuccessful) {
                    // Login successful, navigate to HomeActivity
                    Toast.makeText(applicationContext, "Login Successful..", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                } else {
                    // Login failed, display error message
                    mDialog.dismiss()
                    Toast.makeText(applicationContext, "Login Failed..", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set click listener for "Signup Here" textview to navigate to RegistrationActivity
        mSignupHere.setOnClickListener {
            startActivity(Intent(applicationContext, RegistrationActivity::class.java))
        }

        // Set click listener for "Forget Password" textview to navigate to ResetActivity
        mForgetPassword.setOnClickListener {
            startActivity(Intent(applicationContext, ResetActivity::class.java))
        }
    }
}
