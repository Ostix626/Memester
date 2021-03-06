package com.memester.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Model.Comment
import com.memester.Model.User
import com.memester.ProfileActivity
import com.memester.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.comment_item_layout.view.*

class CommentsAdapter(private val mContext: Context, private val mComment : MutableList<Comment>?) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, position: Int)
    {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val comment = mComment!![position]
        holder.commentTV.text = comment.getComment()
        getUserInfo(holder.imageProfile, holder.userNameTV, comment.getPublisher())

        holder.userNameTV.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", comment.getPublisher())
            pref.apply()

            val prefs = comment.getPublisher()

            val intent = Intent(holder.userNameTV.context, ProfileActivity::class.java)
            intent.putExtra("uid", prefs)
            holder.userNameTV.context.startActivity(intent)
        })

        holder.imageProfile.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", comment.getPublisher())
            pref.apply()

            val prefs = comment.getPublisher()

            val intent = Intent(holder.userNameTV.context, ProfileActivity::class.java)
            intent.putExtra("uid", prefs)
            holder.userNameTV.context.startActivity(intent)
        })
    }

    private fun getUserInfo(imageProfile: CircleImageView, userNameTV: TextView, publisher: String)
    {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageProfile)
                    userNameTV.text = user!!.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) { }

        })


    }

//    private fun getUserInfo(imageProfile: CircleImageView, userNameTV: TextView, publisher: String, function: () -> Unit)
//    {
//        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)
//
//        userRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists())
//                {
//                    val  user = snapshot.getValue(User::class.java)
//                    //Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageProfile)
//                    userNameTV.text = user!!.getUsername()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//
//    }

    override fun getItemCount(): Int { return mComment!!.size }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var imageProfile : de.hdodenhof.circleimageview.CircleImageView
        var userNameTV : TextView
        var commentTV: TextView

        init {
            imageProfile = itemView.findViewById(R.id.user_profilePic_comment)
            userNameTV = itemView.findViewById(R.id.user_name_comment)
            commentTV = itemView.findViewById(R.id.comment)
        }
    }

}
