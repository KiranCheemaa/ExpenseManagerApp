package com.example.expensemanager

import  com.example.expensemanager.RegistrationActivity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mEmail: EditText
    private lateinit var mPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var mForgetPassword: TextView
    private lateinit var mSignupHere: TextView
    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

//        if (mAuth.currentUser != null) {
//            startActivity(Intent(applicationContext, HomeActivity::class.java))
//        }

        mDialog = ProgressDialog(this)

        // Initialize UI elements
        mEmail = findViewById(R.id.email_login)
        mPass = findViewById(R.id.password_login)
        btnLogin = findViewById(R.id.btn_login)
        mForgetPassword = findViewById(R.id.forget_password)
        mSignupHere = findViewById(R.id.signup_reg)

        // Set click listeners
        btnLogin.setOnClickListener {
            val email = mEmail.text.toString().trim()
            val pass = mPass.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                mEmail.error = "Email Required.."
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(pass)) {
                mPass.error = "Password is Required.."
                return@setOnClickListener
            }

            mDialog.setMessage("Processing..")
            mDialog.show()
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                mDialog.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Login Successful..", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                } else {
                    mDialog.dismiss()
                    Toast.makeText(applicationContext, "Login Failed..", Toast.LENGTH_SHORT).show()
                }
            }
        }

        mSignupHere.setOnClickListener {
            startActivity(Intent(applicationContext, RegistrationActivity::class.java))
        }

        mForgetPassword.setOnClickListener {
            startActivity(Intent(applicationContext, ResetActivity::class.java))
        }
    }
}
