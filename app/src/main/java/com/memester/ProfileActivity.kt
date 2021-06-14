package com.memester

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.memester.Adapter.MyMemesAdapter
import com.memester.Model.Post
import com.memester.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var db: DatabaseReference
    var postList: List<Post>? = null
    var myMemesAdapter : MyMemesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        db = FirebaseDatabase.getInstance().reference
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)
        /*val btn = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profilePic)
        btn.setOnClickListener {
            Toast.makeText(this, "hello there", Toast.LENGTH_LONG).show()
        }*/

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(pref != null) {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if(profileId == firebaseUser.uid) {
            follow_btn_profile.visibility = View.GONE
        }else if(profileId != firebaseUser.uid) {
            checkFollowAndFollowing()
        }

        getFollowers()
        getFollowings()
        userInfo()

//        if(follow_btn_profile.text.toString() == "Follow") {
//            follow_btn_profile.setTextColor(Color.parseColor("#00bfff"))
//        } else {
//            follow_btn_profile.setTextColor(Color.WHITE)
//        }

        follow_btn_profile.setOnClickListener {
            if(follow_btn_profile.text.toString() == "Follow")
            {
                follow_btn_profile.setTextColor(Color.WHITE)

                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString()).child("Following").child(profileId)
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference.child("Follow").child(profileId).child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                            {

                                            }
                                        }
                                }
                            }
                        }
                }
                addNotification(profileId)
            }
            else if(follow_btn_profile.text.toString() == "Following")
            {
                follow_btn_profile.setTextColor(Color.parseColor("#00bfff"))

                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString()).child("Following").child(profileId)
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference.child("Follow").child(profileId).child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                            {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }

        followers_layout.setOnClickListener {
            val intent = Intent(this, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }

        following_layout.setOnClickListener {
            val intent = Intent(this, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }

        var recyclerViewUploadedImages : RecyclerView
        recyclerViewUploadedImages = findViewById(R.id.recycler_view_uploaded_pics)
        recyclerViewUploadedImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(this@ProfileActivity, 3)
        recyclerViewUploadedImages.layoutManager = linearLayoutManager

        postList = ArrayList()
        myMemesAdapter = this@ProfileActivity.let { MyMemesAdapter(it, postList as ArrayList<Post>) }
        recyclerViewUploadedImages.adapter = myMemesAdapter

        myMemes()
        /*
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragmentProfile)

        bottomNavigationView.setupWithNavController(navController)
        */
    }

    private fun checkFollowAndFollowing() {
        val followingRef = firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference.child ("Follow").child(it.toString())
                .child("Following")
        }

        if(followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child(profileId).exists()){
                        follow_btn_profile?.text = "Following"
                        follow_btn_profile.setTextColor(Color.WHITE)
                    }else {
                        follow_btn_profile?.text = "Follow"
                        follow_btn_profile.setTextColor(Color.parseColor("#00bfff"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference.child ("Follow").child(profileId)
                .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    followers_profile_fragment?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowings() {
        val followersRef = FirebaseDatabase.getInstance().reference.child ("Follow").child(profileId)
                .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    following_profile_fragment?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                /*if(this@ProfileActivity != null) {
                    return
                }*/

                if(snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_picture)
                    username_profile.text = user!!.getUsername()


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        db.child("Posts").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    var count = 0
                    for (meme in snapshot.children)
                    {
                        val post = meme.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(profileId))
                        {
                            count += 1
                        }
                    }
                    posts_profile_fragment.text = count.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("profileId", firebaseUser.uid)
        pref.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("profileId", firebaseUser.uid)
        pref.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("profileId", firebaseUser.uid)
        pref.apply()
    }

    private fun addNotification(userId: String)
    {
        val notiRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(userId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "Started following you"
        notiMap["postid"] = ""
        notiMap["ispost"] = false

        notiRef.push().setValue(notiMap)
    }

    private fun myMemes()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    (postList as ArrayList<Post>).clear()
                    for (meme in snapshot.children)
                    {
                        val post = meme.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(profileId))
                        {
                            (postList as ArrayList<Post>).add(post)
                        }
                        Collections.reverse(postList)
                        myMemesAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}