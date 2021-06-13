package com.memester

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val regBtn = findViewById<Button>(R.id.signup_btn)

        regBtn.setOnClickListener {
            CreateAccount()
        }
    }

    private fun CreateAccount()
    {
        val email = email_signup.text.toString()
        val username = username_signup.text.toString()
        val password = password_signup.text.toString()
        //val usernameLower = username_signup.text.toString().toLowerCase()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Just make up some email", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(username) -> Toast.makeText(this, "Bob is a nice name...?", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Put 1234567890 for the password", Toast.LENGTH_SHORT).show()

            else -> {
                val progressDialog = ProgressDialog(this@RegistrationActivity, R.style.MyAlertDialogStyle)
                progressDialog.setTitle("Please wait")
                progressDialog.setMessage("Selling your personal information...")

                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful)
                        {
                            saveUserInfo(username, email, progressDialog, username.toLowerCase())
                        }
                        else
                        {
                            val message = task.exception!!.toString()
//                            Toast.makeText(this, "Oopsie doopsie: $message", Toast.LENGTH_LONG).show()
                            Toast.makeText(this, "Oopsie doopsie: Firebase says NO!", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                }
            }
        }
    }

    private fun saveUserInfo(username: String, email: String, progressDialog: ProgressDialog, usernameLower: String)
    {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["username"] = username
        userMap["usernameLower"] = usernameLower
        userMap["email"] = email
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/memester-ce3c4.appspot.com/o/Default%20images%2Fprofile.png?alt=media&token=7914addd-d04e-44cb-b006-5b91e4bfe5ed"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Welcome to the Memester!", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@RegistrationActivity, NavActivity::class.java)
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