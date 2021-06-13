package com.memester

import NotificationAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Model.Notification
import java.util.*
import kotlin.collections.ArrayList

class NotificationsActivity : AppCompatActivity()
{
    private var notificationList: List<Notification>? = null
    private var notificationAdapter: NotificationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        var reciclerView : RecyclerView
        reciclerView = findViewById(R.id.recycler_view_notifications)
        reciclerView?.setHasFixedSize(true)
        reciclerView?.layoutManager = LinearLayoutManager(this)

        notificationList = ArrayList()

        notificationAdapter = NotificationAdapter(this, notificationList as ArrayList<Notification>)
        reciclerView.adapter = notificationAdapter

        readNOtifications()

    }

    private fun readNOtifications()
    {
        val notiRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(FirebaseAuth.getInstance().currentUser!!.uid)

        notiRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.exists())
                {
                    (notificationList as ArrayList<Notification>).clear()

                    for (snapshot in datasnapshot.children)
                    {
                        val notification = snapshot.getValue(Notification::class.java)

                        (notificationList as ArrayList<Notification>).add(notification!!)
                    }
                    Collections.reverse(notificationList)
                    notificationAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}