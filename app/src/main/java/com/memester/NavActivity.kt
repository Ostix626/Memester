package com.memester

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavActivity : AppCompatActivity() {




    /*   fragment selector
    internal var selectedFragment: Fragment? = null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                selectedFragment = Home()
                //moveToFragment(Home())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                selectedFragment = SearchFragment()
                //moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                selectedFragment = ProfileFragment()
                //moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        if (selectedFragment != null){
            supportFragmentManager.beginTransaction().replace(

            )
        }
        false
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        val addMemeBtn: androidx.appcompat.widget.AppCompatImageButton = findViewById(R.id.addMeme)
        val notificationsBtn: androidx.appcompat.widget.AppCompatImageButton =findViewById(R.id.notifications)

        addMemeBtn.setOnClickListener {
            startActivity(Intent(this@NavActivity, AddPostActivity::class.java))
        }
        notificationsBtn.setOnClickListener {
            startActivity(Intent(this@NavActivity, NotificationsActivity::class.java))
            //Toast.makeText(this, "TODO: add notifications", Toast.LENGTH_SHORT).show()
        }


        //bottom menu on profile
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)

        bottomNavigationView.setupWithNavController(navController)
    }
}