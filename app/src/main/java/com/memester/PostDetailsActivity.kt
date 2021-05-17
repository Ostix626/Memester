package com.memester

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Adapter.PostAdapter
import com.memester.Model.Post

class PostDetailsActivity : AppCompatActivity() {
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var postId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        val preferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (preferences != null)
        {
            postId = preferences.getString("postId", "none").toString()
        }

        var recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recycler_view_post_details)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView?.adapter = postAdapter

        retrivePosts()
    }

    private fun retrivePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)
        postsRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                postList?.clear()

                val post = snapshot.getValue(Post::class.java)
                postList!!.add(post!!)

                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}