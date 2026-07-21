package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.api.RetrofitInstance
import com.example.myapplication.model.User
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val tvLoginRedirect = findViewById<TextView>(R.id.tvLoginRedirect)

        btnSignup.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val newUser = User(name = email.substringBefore("@"), email = email, password = password)

            lifecycleScope.launch {
                try {
                    val response = RetrofitInstance.api.registerUser(newUser)
                    if (response.isSuccessful) {
                        Toast.makeText(this@SignupActivity, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignupActivity, "Signup Failed!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@SignupActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvLoginRedirect.setOnClickListener {
            try {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Error opening Login: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}