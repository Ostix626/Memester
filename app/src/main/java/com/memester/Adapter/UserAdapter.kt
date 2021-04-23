package com.memester.Adapter

import android.app.PendingIntent.getActivity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.util.Log.INFO
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Model.User
import com.memester.R
import com.squareup.picasso.Picasso
import okhttp3.internal.platform.Platform.INFO
import java.util.logging.Level.INFO

class UserAdapter (private var mContext: Context, private var mUser: List<User>, private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTextView.text = user.getUsername()

        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.userProfileImage)

        checkFollowStatus(user.getUid(), holder.followButton)

        holder.followButton.setOnClickListener {
            if(holder.followButton.text.toString() == "Follow")
            {
                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString()).child("Following").child(user.getUid())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference.child("Follow").child(user.getUid()).child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                            {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
            else
            {
                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString()).child("Following").child(user.getUid())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference.child("Follow").child(user.getUid()).child("Followers").child(it.toString())
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
    }




    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var userNameTextView: TextView = itemView.findViewById(R.id.username_search)
        var userProfileImage: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.userProfilePic)
        var followButton: Button = itemView.findViewById(R.id.follow_btn_search)
    }

    private fun checkFollowStatus(uid: String, followButtton: Button)
    {
        val followingRef = firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference.child ("Follow").child(it.toString())
                .child("Following")
        }
        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(datasnapshot: DataSnapshot)
            {
                if(datasnapshot.child(uid).key.toString() == FirebaseAuth.getInstance().currentUser.uid){
                    followButtton.visibility = View.INVISIBLE
//                    Toast.makeText(this@UserAdapter.mContext , "prvo $prvi", Toast.LENGTH_LONG).show()
//                    Toast.makeText(this@UserAdapter.mContext , "prvo $drugi", Toast.LENGTH_LONG).show()
                }
                else if(datasnapshot.child(uid).exists())
                {
//                    var prvi: String = datasnapshot.child(uid).key.toString()
//                    var drugi: String = FirebaseAuth.getInstance().currentUser.uid
//                    Log.d("ALA", "prvi: $prvi")
//                    Log.d("ALA", "drugi: $drugi")
                    followButtton.text = "Following"
                    followButtton.setTextColor(Color.WHITE)
                }
                else
                {
                    followButtton.text = "Follow"
                    followButtton.setTextColor(Color.parseColor("#00bfff"))
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }

        })
    }
}