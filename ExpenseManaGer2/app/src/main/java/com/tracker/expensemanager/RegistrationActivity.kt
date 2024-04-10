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
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth

// RegistrationActivity class
class RegistrationActivity : AppCompatActivity(), GoogleSignInManager.SignInCallback {

    // Properties declaration
    private lateinit var mEmail: EditText
    private lateinit var mPass: EditText
    private lateinit var btnReg: Button
    private lateinit var mSignin: TextView
    private lateinit var btnGoogleSignIn: SignInButton
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDialog: ProgressDialog
    private lateinit var googleSignInManager: GoogleSignInManager

    // onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize FirebaseAuth instance and ProgressDialog
        mAuth = FirebaseAuth.getInstance()
        mDialog = ProgressDialog(this)

        // Initialize UI elements
        mEmail = findViewById(R.id.email_reg)
        mPass = findViewById(R.id.password_reg)
        btnReg = findViewById(R.id.btn_reg)
        mSignin = findViewById(R.id.signin_here)
        btnGoogleSignIn = findViewById(R.id.btngooglesignin)

        // Initialize GoogleSignInManager
        googleSignInManager = GoogleSignInManager(this, this)

        // Set click listener for registration button
        btnReg.setOnClickListener {
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

            // Create user with email and password
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                mDialog.dismiss()
                if (task.isSuccessful) {
                    // Registration successful, navigate to MainActivity
                    Toast.makeText(applicationContext, "Registration Complete", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                } else {
                    // Registration failed, display error message
                    Toast.makeText(applicationContext, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set click listener for "Sign in Here" textview to navigate to MainActivity
        mSignin.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        // Set click listener for Google sign-in button
        btnGoogleSignIn.setOnClickListener {
            googleSignInManager.signIn()
        }
    }

    // Override onActivityResult to handle Google sign-in result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleSignInManager.onActivityResult(requestCode, resultCode, data)
    }

    // Implement onSignInSuccess to handle successful Google sign-in
    override fun onSignInSuccess() {
        // Handle sign-in success by navigating to HomeActivity
        startActivity(Intent(this, HomeActivity::class.java))
        finish() // Finish the current activity after navigation
    }

    // Implement onSignInFailure to handle failed Google sign-in
    override fun onSignInFailure(errorMessage: String) {
        // Handle sign-in failure by displaying error message
        Toast.makeText(this, "Google Sign-In failed: $errorMessage", Toast.LENGTH_SHORT).show()
    }
}
