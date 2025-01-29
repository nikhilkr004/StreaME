package com.example.visionary.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.visionary.DataClass.UserData
import com.example.visionary.MainActivity
import com.example.visionary.R
import com.example.visionary.Utils
import com.example.visionary.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var refrence: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        refrence = FirebaseDatabase.getInstance().reference


        binding.enter.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()


            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill the all Require Details", Toast.LENGTH_SHORT).show()
            } else {
                signup(name, email, password)
                Utils.showDialog(this@SignUpActivity,"signing up...")
            }
        }

    }

    private fun signup(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val UserID = firebaseAuth.currentUser!!.uid
                    val userData = UserData(
                       name=name,
                        email=email,
                        password=password,
                        userId = UserID,
                        isSubscribe ="false"

                    )

                    val ref = refrence.child("user").child(UserID)
                    ref.setValue(userData).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Utils.showDialog(this,"sign up completed")
                            startActivity(
                                Intent(
                                    this@SignUpActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }

                    }

                } else {
                    Utils.hideDialog()
                    Toast.makeText(
                        this@SignUpActivity,
                        it.exception!!.localizedMessage,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }
}