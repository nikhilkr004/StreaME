package com.example.visionary.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.visionary.MainActivity
import com.example.visionary.R
import com.example.visionary.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        if (FirebaseAuth.getInstance().currentUser!=null){
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
        else{
            binding.signInBtn.setOnClickListener {
                startActivity(Intent(this,SignInActivity::class.java))
            }

            binding.signUpActivity.setOnClickListener {
                startActivity(Intent(this,SignUpActivity::class.java))
            }
        }

    }
}