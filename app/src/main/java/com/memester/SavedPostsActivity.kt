package com.memester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Adapter.MyMemesAdapter
import com.memester.Model.Post

class SavedPostsActivity : AppCompatActivity() {
    var mySavedMemesAdapter: MyMemesAdapter? = null
    var postListSaved: List<Post>? = null
    var mySavedMemes: List<String>? = null
    val currentUser = FirebaseAuth.getInstance().currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_posts)


        var recyclerViewSavedImages : RecyclerView
        recyclerViewSavedImages = findViewById(R.id.recycler_view_uploaded_pics)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(this, 3)
        recyclerViewSavedImages.layoutManager = linearLayoutManager

        postListSaved = ArrayList()
        mySavedMemesAdapter = let { MyMemesAdapter(it, postListSaved as ArrayList<Post>) }
        recyclerViewSavedImages.adapter = mySavedMemesAdapter

        mySaves()
    }

    private fun mySaves()
    {
        mySavedMemes = ArrayList()

        val savedRef = FirebaseDatabase.getInstance().reference.child("Saves").child(currentUser!!.uid)
        savedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    for (meme in snapshot.children)
                    {
                        (mySavedMemes as ArrayList<String>).add(meme.key!!)
                    }
                    readSavedMemesData()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun readSavedMemesData()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                {
                    (postListSaved as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children)
                    {
                        val post = snapshot.getValue(Post::class.java)

                        for (key in mySavedMemes!!)
                        {
                            if (post!!.getPostid() == key)
                            {
                                (postListSaved as ArrayList<Post>).add(post!!)
                            }
                        }
                    }
                    mySavedMemesAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}