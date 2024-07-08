package com.example.todolist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.todolist.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var client: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.SignupBtn.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val pass = binding.password.text.toString().trim()
            val con = binding.conpassword.text.toString().trim()
            if (name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && con.isNotEmpty()) {
                if (pass.length >= 8) {
                    if (pass == con) {
                        auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener {
                            Toast.makeText(this, "Signed up Successfully!", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                        }

                    } else {
                        Toast.makeText(this, "Passwords should match!", Toast.LENGTH_SHORT).show()

                    }
                } else {
                    Toast.makeText(
                        this,
                        "Password should at least be 8 characters!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            } else {
                Toast.makeText(this, "Fields can't be Empty!", Toast.LENGTH_SHORT).show()

            }
        }

        binding.googleSignupBtn.setOnClickListener {
            signinWithGoogle()
        }

    }

    private fun signinWithGoogle() {
        val webClientId = getString(R.string.webClientID)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        client = GoogleSignIn.getClient(this, gso)
        val signInIntent = client.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        auth.signInWithCredential(credential).addOnSuccessListener {
                            Toast.makeText(this, "Signup successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to login!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: ApiException) {
                    Toast.makeText(this, "Sign in failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
}