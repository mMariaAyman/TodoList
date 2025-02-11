package com.example.todolist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.todolist.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var binding:ActivityMainBinding
    private lateinit var client: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val pass = binding.password.text.toString().trim()
            if(email.isNotEmpty() && pass.isNotEmpty()) {

                auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener {
                    Toast.makeText(this, "Logged in Successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to Sign in, Wrong Email or Password !", Toast.LENGTH_SHORT).show()

                }
            }else{
                Toast.makeText(this, "Please Enter all fields!", Toast.LENGTH_SHORT).show()

            }
        }

        binding.SignupBtn.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        binding.googleloginBtn.setOnClickListener{
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
                            Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show()
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