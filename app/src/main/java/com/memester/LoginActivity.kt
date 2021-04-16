package com.memester

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.signup_link_btn).setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        val loginBtn = findViewById<Button>(R.id.login_btn)
        loginBtn.setOnClickListener { loginUser() }
    }

    private fun loginUser()
    {
        val email = email_login.text.toString()
        val password = password_login.text.toString()

        when
        {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Did you forget your made up email?", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "The password is kinda wrong...", Toast.LENGTH_SHORT).show()

            else -> {
                val progressDialog = ProgressDialog(this@LoginActivity, R.style.MyAlertDialogStyle)
                progressDialog.setTitle("Please wait")
                progressDialog.setMessage("Stealing your data...")

                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@LoginActivity, NavActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Oopsie doopsie: $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }


    override fun onStart()
    {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null)
        {
            val intent = Intent(this@LoginActivity, NavActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}