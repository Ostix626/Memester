package com.memester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Adapter.CommentsAdapter
import com.memester.Model.Comment
import com.memester.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentsAdapter? = null
    private var commentList: MutableList<Comment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postId = intent.getStringExtra("postId")
        publisherId = intent.getStringExtra("publisherId")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        var recyclerView: RecyclerView = findViewById(R.id.recycler_view_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapter(this, commentList) // as ArrayList<Comment>
        recyclerView.adapter = commentAdapter

        userInfo()
        readComments()
        getPostImage()

        post_comment.setOnClickListener(View.OnClickListener {
            if(post_new_comment!!.text.toString() == "")
            {
                Toast.makeText(this@CommentsActivity, "Do you know how to write?", Toast.LENGTH_SHORT).show()
            }
            else { addComment() }
        })
    }

    private fun addComment()
    {
        val commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = post_new_comment.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid
        commentsRef.push().setValue(commentsMap)
        addNotification()
        post_new_comment!!.text.clear()
    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_comment)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getPostImage()
    {
        val postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId!!).child("postimage")

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val image = snapshot.getValue().toString()
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(comments_image_post)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun readComments()
    {
        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    commentList!!.clear()
                    for (comm in snapshot.children)
                    {
                        val com = comm.getValue(Comment::class.java)
                        commentList!!.add(com!!)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addNotification()
    {
        val notiRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(publisherId!!)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "Commented: " + post_new_comment!!.text.toString()
        notiMap["postid"] = postId
        notiMap["ispost"] = true

        notiRef.push().setValue(notiMap)
    }

}