package com.memester

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Model.Notification
import com.memester.Model.User
import java.util.*

class NavActivity : AppCompatActivity() {


    val CHANNEL_ID = "notifications"
    val CHANNEL_NAME = "notification"
    var userNameNotification = ""
    lateinit var notificationsBtn : androidx.appcompat.widget.AppCompatImageButton

    fun createNotificaitonChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


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

        createNotificaitonChannel()

        val addMemeBtn: androidx.appcompat.widget.AppCompatImageButton = findViewById(R.id.addMeme)
        notificationsBtn = findViewById(R.id.notifications)


        addMemeBtn.setOnClickListener {
            startActivity(Intent(this@NavActivity, AddPostActivity::class.java))
//            startActivity(Intent(this@NavActivity, MainActivity::class.java))
        }
        notificationsBtn.setOnClickListener {
            notificationsBtn.setImageResource(R.drawable.bell)
            startActivity(Intent(this@NavActivity, NotificationsActivity::class.java))
            //Toast.makeText(this, "TODO: add notifications", Toast.LENGTH_SHORT).show()
        }


        //bottom menu on profile
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)

        bottomNavigationView.setupWithNavController(navController)

        checkNotifications(notificationsBtn)
    }

    private fun checkNotifications(notificationsBtn: androidx.appcompat.widget.AppCompatImageButton)
    {
        val notiRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(FirebaseAuth.getInstance().currentUser!!.uid)

        notiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.exists()) {
                    notificationsBtn.setImageResource(R.drawable.bell_red)
                    val text = datasnapshot.children.last().getValue(Notification::class.java)?.getText()
                    val userID = datasnapshot.children.last().getValue(Notification::class.java)?.getUserId().toString()

                    val runningAppProcessInfo = ActivityManager.RunningAppProcessInfo()
                    ActivityManager.getMyMemoryState(runningAppProcessInfo)
                    val appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND


                    if (text != null && appRunningBackground) {
                        getDataForNotification(userID, text)
//                        notificationsBtn.setImageResource(R.drawable.bell)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun pushNotification(text: String, title: String)
    {
//        notificationsBtn.setImageResource(R.drawable.bell_red)
        val intent = Intent(this, NavActivity::class.java)
        val intentNotif = Intent(this, NotificationsActivity::class.java)
//        val pendingIntent = TaskStackBuilder.create(this).run {
//            addNextIntentWithParentStack(intent)
//            getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT)
//        }

        val PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

//        intent.apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent = PendingIntent.getActivity(this,0,intent,0)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(PendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val notificationId: Long = System.currentTimeMillis()

        notificationManager.notify(0, notification)
    }

    private fun getDataForNotification(publisherId: String, text: String){
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(
            publisherId
        )

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)
                    pushNotification(text, user!!.getUsername())

                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}


