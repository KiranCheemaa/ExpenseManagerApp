// RegistrationActivity.kt
package com.example.expensemanager

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity(), GoogleSignInManager.SignInCallback {

    private lateinit var mEmail: EditText
    private lateinit var mPass: EditText
    private lateinit var btnReg: Button
    private lateinit var mSignin: TextView
    private lateinit var btnGoogleSignIn: SignInButton

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDialog: ProgressDialog

    private lateinit var googleSignInManager: GoogleSignInManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        mAuth = FirebaseAuth.getInstance()
        mDialog = ProgressDialog(this)

        mEmail = findViewById(R.id.email_reg)
        mPass = findViewById(R.id.password_reg)
        btnReg = findViewById(R.id.btn_reg)
        mSignin = findViewById(R.id.signin_here)
        btnGoogleSignIn = findViewById(R.id.btngooglesignin)

        googleSignInManager = GoogleSignInManager(this, this)

        btnReg.setOnClickListener {
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

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                mDialog.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Registration Complete", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Registration Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        mSignin.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        btnGoogleSignIn.setOnClickListener {
            googleSignInManager.signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleSignInManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSignInSuccess() {
        // Handle sign-in success
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun onSignInFailure(errorMessage: String) {
        // Handle sign-in failure
        Toast.makeText(this, "Google Sign-In failed: $errorMessage", Toast.LENGTH_SHORT).show()
    }
}
