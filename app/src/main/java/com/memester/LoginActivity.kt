package com.memester

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registrationBtn = findViewById<Button>(R.id.signup)

        registrationBtn.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        val loginBtn = findViewById<Button>(R.id.login)

        loginBtn.setOnClickListener {
            val intent = Intent(this, NavActivity::class.java)
            startActivity(intent)
        }
    }
}