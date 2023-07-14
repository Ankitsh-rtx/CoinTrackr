package com.example.cointrackr

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cointrackr.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
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
        binding.loginBtn.setOnClickListener {

            val email = binding.username.text.toString().trim()
            val pass = binding.password.text.toString().trim()
            if (email.isEmpty()) {
                binding.username.error = "*required"
            }
            if (pass.isEmpty()) {
                binding.password.error = "*required"
            } else if (email.isNotEmpty() && pass.isNotEmpty()) {
                binding.password.error = null
                binding.username.error = null
                binding.statusBar.visibility= View.VISIBLE

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        checkEmailVerificationStatus(email)
                    }
                }.addOnFailureListener { exception ->
                    binding.statusBar.visibility= View.INVISIBLE
                    Toast.makeText(
                        applicationContext,
                        exception.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }

        binding.forgetPass.setOnClickListener {
            val intent = Intent(this, ForgetActivity::class.java)
            startActivity(intent)

        }

        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent);

        }
    }

    private fun checkEmailVerificationStatus(email: String) {
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser;
        if (firebaseUser != null) {
            binding.statusBar.visibility= View.INVISIBLE

            if (firebaseUser.isEmailVerified) {
                Toast.makeText(this, "successfully Logged in", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(applicationContext, "Please verify your email", Toast.LENGTH_LONG)
                    .show()
                firebaseAuth.signOut()
            }
        }
    }
}