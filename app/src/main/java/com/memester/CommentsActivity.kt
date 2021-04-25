package com.memester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postId = intent.getStringExtra("postId")
        publisherId = intent.getStringExtra("publisherId")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        userInfo()

        post_comment.setOnClickListener(View.OnClickListener {
            if(comment!!.text.toString() == "")
            {
                Toast.makeText(this@CommentsActivity, "Do you know how to write?", Toast.LENGTH_SHORT).show()
            }
            else { addComent() }
        })
    }

    private fun addComent()
    {
        val commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = comment.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid
        commentsRef.push().setValue(commentsMap)
        comment!!.text.clear()
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

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

}