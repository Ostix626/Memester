package com.memester

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Adapter.UserAdapter
import com.memester.Model.User
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_show_users.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class ShowUsersActivity : AppCompatActivity() {
    var id : String = ""
    var title : String = ""

    var userAdapter : UserAdapter? = null
    var userList : List<User>? = null
    var idList : List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)

        val intent = intent
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")


        val toolbar : Toolbar = findViewById(R.id.toolbar)
        val toolbarTitle : TextView = findViewById(R.id.title_text)
        toolbarTitle.text = title.capitalize() + ":"
//        setSupportActionBar(toolbar)
//
//        supportActionBar!!.title = title.toUpperCase()
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        toolbar.setNavigationOnClickListener {
//            finish()
//        }

        var recyclerView : RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()
        userAdapter = UserAdapter(this, userList as ArrayList<User>, false)
        recyclerView.adapter = userAdapter

        idList = ArrayList()

        when(title) {
            "likes" -> getLikes()
            "following" -> getFollowing()
            "followers" -> getFollowers()

        }
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference.child ("Follow").child(id!!)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (idList as ArrayList<String>).clear()

                for(snap in snapshot.children) {
                    (idList as ArrayList<String>).add(snap.key!!)
                }
                showUsers()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowing() {
        val followersRef = FirebaseDatabase.getInstance().reference.child ("Follow").child(id!!)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (idList as ArrayList<String>).clear()

                for(snap in snapshot.children) {
                    (idList as ArrayList<String>).add(snap.key!!)
                }
                showUsers()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getLikes() {
        val LikesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(id!!)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                        (idList as ArrayList<String>).clear()

                    for (snap in snapshot.children) {
                        (idList as ArrayList<String>).add(snap.key!!)
                    }

                    showUsers()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showUsers() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")

        usersRef.addValueEventListener (object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                (userList as ArrayList<User>).clear()

                for (snapshot in dataSnapshot.children)
                {
                    val user = snapshot.getValue(User::class.java)

                    for(id in idList!!) {
                        if (user!!.getUid() == id)
                        {
                            (userList as ArrayList<User>).add(user!!)
                        }
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }
}