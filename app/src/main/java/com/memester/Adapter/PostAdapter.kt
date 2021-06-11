package com.memester.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.CommentsActivity
import com.memester.Model.Post
import com.memester.Model.User
import com.memester.ProfileActivity
import com.memester.R
import com.memester.ShowUsersActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_comments.*

class PostAdapter
    (private val mContext: Context,
     private val mPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Picasso.get().load(post.getPostimage()).into(holder.postImage)

        if (post.getDescription().equals("")) { holder.description.visibility = View.GONE }
        else
        {
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.getDescription())
        }

        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.getPublisher())
        isLiked(post.getPostid(), holder.likeButton)
        numberOfLikes(holder.likes, post.getPostid())
        numberOfComments(holder.comments, post.getPostid())
        checkSaveStatus(post.getPostid(), holder.saveButton)

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like")
            {
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPostid()).child(firebaseUser!!.uid).setValue(true)
                addNotification(post.getPublisher(), post.getPostid())
            }
            else
            {
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPostid()).child(firebaseUser!!.uid).removeValue()
            }
        }

        holder.commentButton.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.saveButton.setOnClickListener {
            if (holder.saveButton.tag == "Save")
            {
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid).child(post.getPostid()).setValue(true)
            }
            else
            {
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid).child(post.getPostid()).removeValue()
            }
        }

        holder.profileImage.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", post.getPublisher())
            pref.apply()

            val prefs = post.getPublisher()

            val intent = Intent(holder.itemView.context, ProfileActivity::class.java)
            intent.putExtra("uid", prefs)
            mContext.startActivity(intent)
        })

        holder.userName.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", post.getPublisher())
            pref.apply()

            val prefs = post.getPublisher()

            val intent = Intent(holder.itemView.context, ProfileActivity::class.java)
            intent.putExtra("uid", prefs)
            mContext.startActivity(intent)
        })

        holder.likes.setOnClickListener {
            val intent = Intent(mContext, ShowUsersActivity::class.java)
            intent.putExtra("id", post.getPostid())
            intent.putExtra("title", "likes")
            mContext.startActivity(intent)
        }
    }

    private fun numberOfLikes(likes: TextView, postid: String)
    {
        val LikesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) { likes.text = snapshot.childrenCount.toString() }
                else {likes.text = "0"}
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun numberOfComments(comments: TextView, postid: String)
    {
        val comentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postid)

        comentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) { comments.text = snapshot.childrenCount.toString() }
                else {comments.text = "0"}
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun isLiked(postid: String, likeButton: ImageView, )
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val LikesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(firebaseUser!!.uid).exists())
                {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = "Liked"
                }
                else
                {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag = "Like"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun getItemCount(): Int { return mPost.size }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init
        {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_save_btn)
            userName = itemView.findViewById(R.id.user_name_home)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
        }



    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    userName.setText(user!!.getUsername())
                    publisher.setText(user!!.getUsername())
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun checkSaveStatus(postid: String, imageView: ImageView)
    {
        val savesRef = FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid)

        savesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.saved)
                    imageView.tag = "Saved"
                }
                else
                {
                    imageView.setImageResource(R.drawable.not_saved)
                    imageView.tag = "Save"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addNotification(userId: String, postId: String)
    {
        val notiRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(userId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "Liked your post"
        notiMap["postid"] = postId
        notiMap["ispost"] = true

        notiRef.push().setValue(notiMap)
    }

}