// GoogleSignInManager.kt
package com.tracker.expensemanager

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.tracker.expensemanager.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.api.ApiException

// GoogleSignInManager class
class GoogleSignInManager(private val activity: Activity, private val callback: SignInCallback) {

    private val TAG = "GoogleSignInManager"
    private val RC_SIGN_IN = 9001
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    // Initialization block
    init {
        auth = FirebaseAuth.getInstance()

        // Configure Google sign-in to request user ID, email address, and basic profile
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    // Method to start Google sign-in flow
    fun signIn() {
        Log.d(TAG, "signIn() method called")
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Method to handle result of Google sign-in activity
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult() method called with requestCode=$requestCode")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed", e)
                callback.onSignInFailure("Google sign in failed: ${e.message}")
            }
        }
    }

    // Method to authenticate with Firebase using Google credentials
    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG, "firebaseAuthWithGoogle() method called")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(activity, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    callback.onSignInSuccess()
                } else {
                    callback.onSignInFailure("Authentication failed")
                }
            }
    }

    // Interface for sign-in callbacks
    interface SignInCallback {
        fun onSignInSuccess()
        fun onSignInFailure(errorMessage: String)
    }
}
