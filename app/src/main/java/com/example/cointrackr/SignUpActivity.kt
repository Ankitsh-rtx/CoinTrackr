package com.example.cointrackr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.cointrackr.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        var passVisible = false;
        binding.passVisibilityBtn.setOnClickListener {
            if(!passVisible) {
                binding.passVisibilityBtn.setImageResource(R.drawable.password_invisible)
                binding.password.transformationMethod = null
                passVisible = true
            }
            else {
                binding.passVisibilityBtn.setImageResource(R.drawable.password_visible)
                binding.password.transformationMethod = PasswordTransformationMethod()
                passVisible = false
            }
        }

        binding.signupBtn.setOnClickListener {
            val email = binding.username.text.toString().trim()
            val pass = binding.password.text.toString().trim()
            if (email.isEmpty()) {
                binding.username.error = "*required"
            }
            if (pass.isEmpty() || pass.length < 8) {
                binding.password.error = "minimum 8 characters"
            } else if (email.isNotEmpty() && pass.isNotEmpty()) {
                binding.statusBar.visibility = View.VISIBLE
                binding.username.error = null
                binding.password.error = null
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "successfully signed-in", Toast.LENGTH_SHORT).show()
                        sendEmailVerification(email)
                    }
                }.addOnFailureListener { exception ->

                    binding.statusBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        applicationContext,
                        exception.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

    }

    private fun sendEmailVerification(email: String) {
        val firebaseUser: FirebaseUser = firebaseAuth.getCurrentUser()!!;
        if (firebaseUser != null) {
            binding.statusBar.visibility = View.INVISIBLE
            firebaseUser.sendEmailVerification().addOnCompleteListener {
                Toast.makeText(
                    applicationContext,
                    "Verification link has been sent to your email!",
                    Toast.LENGTH_LONG
                ).show()
                firebaseAuth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()

            }
        } else {
            Toast.makeText(
                applicationContext,
                "Failed to send Verification link to your email!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}