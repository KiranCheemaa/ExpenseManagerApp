// GoogleSignInManager.kt
package com.example.expensemanager

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.api.ApiException

class GoogleSignInManager(private val activity: Activity, private val callback: SignInCallback) {

    private val TAG = "GoogleSignInManager"
    private val RC_SIGN_IN = 9001
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    init {
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun signIn() {
        Log.d(TAG, "signIn() method called")
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

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

    interface SignInCallback {
        fun onSignInSuccess()
        fun onSignInFailure(errorMessage: String)
    }
}
