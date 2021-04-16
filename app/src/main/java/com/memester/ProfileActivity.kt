package com.memester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)
        val btn = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profilePic)
        btn.setOnClickListener {
            Toast.makeText(this, "hello there", Toast.LENGTH_LONG).show()
        }

        /*
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragmentProfile)

        bottomNavigationView.setupWithNavController(navController)
        */

    }
}