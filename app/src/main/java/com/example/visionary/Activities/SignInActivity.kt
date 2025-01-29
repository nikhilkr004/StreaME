package com.example.visionary.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.visionary.MainActivity
import com.example.visionary.R
import com.example.visionary.Utils
import com.example.visionary.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.enter.setOnClickListener {
            Utils.showDialog(this,"signing in...")
            loginUser()
        }

    }

    private fun loginUser() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "please enter email and password", Toast.LENGTH_SHORT).show()
            Utils.hideDialog()
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Utils.showDialog(this,"sign in finish!....")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Utils.hideDialog()
                    Toast.makeText(this, it.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}